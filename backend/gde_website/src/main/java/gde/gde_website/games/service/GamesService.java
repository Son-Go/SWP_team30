package gde.gde_website.games.service;

import gde.gde_website.games.entity.*;
import gde.gde_website.games.mapper.GamesMapper;
import gde.gde_website.games.model.*;
import gde.gde_website.games.repository.*;
import gde.gde_website.users.entity.UserEntity;
import gde.gde_website.users.model.UserRole;
import gde.gde_website.users.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This class is implementing business logic of requests handlers
 * @Author: Artemii Gorelov, Egor Grishin
 */
@Service
@RequiredArgsConstructor
public class GamesService {
    private static final Logger gamesServiceLogger = LoggerFactory.getLogger(GamesService.class);

    private final GamesRepository gamesRepository;
    private final TagRepository tagRepository;
    private final GameTagRepository gameTagRepository;
    private final GamesMapper mapper;
    private final UsersRepository usersRepository;
    private final GameScreenshotsRepository gameScreenshotsRepository;
    private final TagTypeRepository tagTypeRepository;

    /**
     * Returns a page of all games ordered by creation date descending.
     * For the current page, loads authors in batch and maps each game to response DTO
     * with tags grouped by tag type. Every known tag type is present in response,
     * even if the game has no tags of that type.
     *
     * @param pageable - page request
     * @return paginated list of games with grouped tags and author info
     * @Author: Artemii Gorelov, Egor Grishin
     */
    @Transactional(readOnly = true)
    public Page<GamesPageResponse> getAllGames(Pageable pageable) {
        gamesServiceLogger.info("Called getAllGames method");

        Page<GamesEntity> gamesPage = gamesRepository.findAllByOrderByCreatedAtDesc(pageable);

        Set<Long> authorIds = allAuthorsIdsOnPage(gamesPage);

        Map<Long, UserEntity> authorsMap = usersRepository.findAllById(authorIds).stream()
                .collect(Collectors.toMap(UserEntity::getId, user -> user));

        List<String> tagTypeNames = allTagTypeNames();

        return gamesPage.map(game -> mapper.gamesEntityToGamesPageResponse(game, tagTypeNames, authorsMap));
    }

    /**
     * Returns games filtered by tag names using OR logic.
     * A game is included when it has at least one tag from {@code tagsRequest}.
     * When {@code tagsRequest} is {@code null} or empty, falls back to {@link #getAllGames(Pageable)}.
     * For the current page, authors are loaded in batch and tags are grouped by tag type.
     *
     * @param tagsRequest - list of tag names used for filtering
     * @param pageable - pagination information including page number, size and sort order
     * @return paginated list of matching games, or all games if filter list is empty
     * @Author: Artemii Gorelov, Egor Grishin
     */
    @Transactional(readOnly = true)
    public Page<GamesPageResponse> getGamesByTags(List<String> tagsRequest, Pageable pageable) {
        gamesServiceLogger.info("Called getGamesByTags method with tags {}", tagsRequest);

        if (tagsRequest == null || tagsRequest.isEmpty()) {
            return getAllGames(pageable);
        }

        Page<GamesEntity> gamesPage = gamesRepository.findByTagNames(tagsRequest, pageable);

        Set<Long> authorIds = allAuthorsIdsOnPage(gamesPage);

        Map<Long, UserEntity> authorsMap = usersRepository.findAllById(authorIds).stream()
                .collect(Collectors.toMap(UserEntity::getId, user -> user));

        List<String> tagTypeNames = allTagTypeNames();

        return gamesPage.map(game -> mapper.gamesEntityToGamesPageResponse(game, tagTypeNames, authorsMap));
    }

    /**
     * Returns detailed game card by id.
     * In addition to scalar game fields, includes author information, screenshots,
     * ownership flag for current user, and tags grouped by tag type.
     * Every known tag type is present in response even if the game has no tags of that type.
     *
     * @param gameId - id of the game to get
     * @param currentUserId - current authenticated user id, or {@code null} for anonymous request
     * @return detailed game response object
     * @Author: Egor Grishin, Artemii Gorelov
     */
    @Transactional(readOnly = true)
    public GamesCardResponse getGameById(Long gameId, Long currentUserId) {
        gamesServiceLogger.info("Called GamesService getGameById method");
        GamesEntity game = gamesRepository.findDetailedById(gameId).
                orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        UserEntity author = usersRepository.findById(game.getAuthorId())
                .orElse(null);

        AuthorResponse authorResponse = null;
        if (author != null) {
            authorResponse = new AuthorResponse(
                    author.getUsername(),
                    author.getProfileImageUrl(),
                    author.getEmail()
            );
        }

        List<String> screenshots = gameScreenshotsRepository.findAllByGameId(gameId)
                .stream().map(GamesScreenshotEntity::getUrl).toList();

        return mapper.gamesEntityToGamesCardResponse(game,
                currentUserId,
                authorResponse,
                screenshots,
                allTagTypeNames()
        );
    }

    /**
     * Creates a new game and persists tag and screenshot relations from the request.
     * Tag names from request are validated against existing tags and deduplicated before insertion.
     *
     * @param request - create request with scalar fields, optional flat tag names list and optional screenshots list
     * @param authorId - id of author that creates the game
     * @return created game response preserving flat create/update contract
     * @Author: Artemii Gorelov, Egor Grishin
     */
    @Transactional
    public Games createGame(GamesCreateRequest request, Long authorId) {
        gamesServiceLogger.info("Called GamesService createGame method");
        GamesEntity game = new GamesEntity(
                authorId,
                request.title(),
                request.description(),
                request.bannerUrl()
        );

        GamesEntity savedGame = gamesRepository.save(game);

        if (request.gameTags() != null) {
            List<Integer> tagIds = request.gameTags().stream()
                    .map(tagName -> tagRepository.findByName(tagName)
                            .orElseThrow(() -> new ResponseStatusException(
                                    HttpStatus.BAD_REQUEST, "Tag not found: " + tagName))
                            .getId())
                    .distinct()
                    .toList();

            for (Integer tagId : tagIds) {
                gameTagRepository.insertGameTag(savedGame.getId(), tagId);
            }
        }

        if (request.screenshots() != null) {
            List<GamesScreenshotEntity> screenshots = request.screenshots().stream()
                    .map(url -> new GamesScreenshotEntity(savedGame.getId(), url))
                    .toList();
            gameScreenshotsRepository.saveAll(screenshots);
        }

        return createGamesResponse(savedGame, request.gameTags(), request.screenshots());
    }

    /**
     * Updates an existing game.
     * Only non-null scalar fields from request are applied. When {@code gameTags} is provided,
     * existing game-tag relations are fully replaced with validated and deduplicated request values.
     * When {@code screenshots} is provided, existing screenshots are fully replaced as well.
     *
     * @param request - update request with optional scalar fields, tags and screenshots
     * @param currentUserId - current authenticated user id
     * @param gameId - id of game to be updated
     * @return updated game response preserving flat create/update contract
     * @throws ResponseStatusException with codes:
     * 404 when game does not exist
     * 403 when user is neither author nor admin
     * @Author: Egor Grishin
     */
    @Transactional
    public Games updateGame(UpdateGameRequest request, Long currentUserId, Long gameId) {
        gamesServiceLogger.info("Called GamesService updateGame method");

        checkOwnerOrAdmin(gameId, currentUserId);

        GamesEntity gameToUpdate = gamesRepository.findById(gameId).
                orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (request.title() != null) {
            gameToUpdate.setTitle(request.title());
        }
        if (request.description() != null) {
            gameToUpdate.setDescription(request.description());
        }
        if (request.bannerUrl() != null) {
            gameToUpdate.setBannerUrl(request.bannerUrl());
        }

        if (request.gameTags() != null) {
            gameTagRepository.deleteAllByGameId(gameId);

            List<Integer> tagIds = request.gameTags().stream()
                    .map(tagName -> tagRepository.findByName(tagName)
                            .orElseThrow(() -> new ResponseStatusException(
                                    HttpStatus.BAD_REQUEST, "Tag not found: " + tagName))
                            .getId())
                    .distinct()
                    .toList();

            for (Integer tagId : tagIds) {
                gameTagRepository.insertGameTag(gameId, tagId);
            }
        }

        if (request.screenshots() != null) {
            gameScreenshotsRepository.deleteAllByGameId(gameId);
            List<GamesScreenshotEntity> screenshots = request.screenshots().stream()
                    .map(url -> new GamesScreenshotEntity(gameId, url))
                    .toList();
            gameScreenshotsRepository.saveAll(screenshots);
        }

        GamesEntity savedGame = gamesRepository.save(gameToUpdate);
        gamesServiceLogger.info("Successfully updated game id={}", gameId);


        return createGamesResponse(savedGame, request.gameTags(), request.screenshots());
    }

    /**
     * Deletes game with requested id after ownership or admin permission check.
     *
     * @param gameId - id of game to be deleted
     * @param currentUserId - id of user who wants to delete game
     * @return flat deleted game response
     * @throws ResponseStatusException with codes:
     * 404 when game with requested id is not found
     * 403 when user is neither game author nor admin
     * @Author: Artemii Gorelov, Egor Grishin
     */
    @Transactional
    public Games deleteGame(Long gameId, Long currentUserId) {
        gamesServiceLogger.info("Called GamesService deleteGame method");

        checkOwnerOrAdmin(gameId, currentUserId);

        GamesEntity gameToDelete = gamesRepository.findById(gameId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found"));

        gamesRepository.delete(gameToDelete);

        gamesServiceLogger.info("Successfully deleted game id={}", gameId);
        return mapper.entityToGames(gameToDelete);
    }

    /**
     * Returns author information by author id.
     *
     * @param id - author id to look up
     * @return author response containing username, profile image and email
     * @Author: Artemii Gorelov
     */
    public AuthorResponse getAuthorById(Long id) {
        UserEntity author = usersRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Author with id = " + id + " not found"));

        return new AuthorResponse(
          author.getUsername(),
          author.getProfileImageUrl(),
          author.getEmail()
        );
    }

    /**
     * Returns all available tags grouped by tag type.
     * Every known tag type is present in response even if there are currently no tags of that type.
     *
     * @return new {@link TagsResponse} object containing tag names grouped by tag type
     * @Author: Egor Grishin
     */
    public TagsResponse getAllTags() {
        List<TagEntity> gameTags = tagRepository.findAll();
        List<String> allTagTypeNames = allTagTypeNames();

        Map<String, List<String>> separatedTags = new LinkedHashMap<>();

        for (String tagTypeName : allTagTypeNames) {
            separatedTags.put(tagTypeName, new ArrayList<>());
        }

        for (TagEntity tag : gameTags) {
            separatedTags.get(tag.getTagType().getType()).add(tag.getName());
        }

        return new TagsResponse(separatedTags);
    }

    /**
     * Marks game as approved.
     * Only admin user may perform this operation.
     *
     * @param gameId - id of game to be approved
     * @param adminId - id of admin who is approving game
     * @Author: Artemii Gorelov
     */
    @Transactional
    public void approveGame(Long gameId, Long adminId) {
        UserEntity admin = usersRepository.findById(adminId).orElseThrow();

        if (admin.getRole() != UserRole.ADMIN) throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        GamesEntity game = gamesRepository.findById(gameId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Game with such id not found"));
        game.setApproved(true);
        gamesRepository.save(game);
        gamesServiceLogger.info("Game {} approved by admin {}", gameId, adminId);
    }

    /**
     * Marks game as rejected.
     * Only admin user may perform this operation.
     *
     * @param gameId - id of game to be rejected
     * @param adminId - id of admin who is rejecting game
     * @Author: Artemii Gorelov
     */
    @Transactional
    public void rejectGame(Long gameId, Long adminId) {
        UserEntity admin = usersRepository.findById(adminId).orElseThrow();
        if (admin.getRole() != UserRole.ADMIN) throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        GamesEntity game = gamesRepository.findById(gameId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found"));
        game.setApproved(false);
        gamesRepository.save(game);
        gamesServiceLogger.info("Game {} rejected by admin {}", gameId, adminId);
    }

    /**
     * Updates game description using admin privileges.
     * Only admin user may perform this operation.
     *
     * @param gameId - id of game whose description should be changed
     * @param description - new description
     * @param adminId - id of admin who is changing game description
     * @Author: Artemii Gorelov
     */
    @Transactional
    public void updateGameDescriptionAdmin(Long gameId, String description, Long adminId) {
        UserEntity admin = usersRepository.findById(adminId).orElseThrow();
        if (admin.getRole() != UserRole.ADMIN) throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        GamesEntity game = gamesRepository.findById(gameId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found"));
        game.setDescription(description);
        gamesRepository.save(game);
    }

    /**
     * Deletes all games created by the specified author.
     *
     * @param authorId - author id
     * @Author: Artemii Gorelov
     */
    @Transactional
    public void deleteGamesByAuthor(Long authorId) {
        gamesRepository.deleteAllByAuthorId(authorId);
    }

    /**
     * Creates {@link Games} response object from saved game entity and request-derived collections.
     * Uses scalar fields from provided {@link GamesEntity} and copies tag and screenshot lists
     * exactly as they were passed to the service method.
     *
     * @param game - saved game entity containing persisted scalar fields
     * @param gameTags - list of tag names passed in create/update request
     * @param screenshots - list of screenshot urls passed in create/update request
     * @return response object containing game fields, tags and screenshots
     * @Author: Egor Grishin
     */
    private Games createGamesResponse(GamesEntity game, List<String> gameTags, List<String> screenshots) {
        return new Games(game.getId(),
                game.getAuthorId(),
                game.getTitle(),
                game.getDescription(),
                game.getBannerUrl(),
                game.getCreatedAt(),
                game.getUpdatedAt(),
                gameTags,
                screenshots
        );
    }

    /**
     * Returns all existing tag type names.
     * The resulting list is used to pre-initialize grouped tags maps,
     * so each response contains every known tag type even when some groups are empty.
     *
     * @return ordered list of all tag type names from storage
     */
    private List<String> allTagTypeNames() {
        return tagTypeRepository.findAll().stream().map(TagTypeEntity::getType).toList();
    }

    /**
     * Collects unique author ids for games contained in the current page.
     * Used to load all page authors in batch instead of querying per item.
     *
     * @param gamesPage - page of games
     * @return set of unique author ids referenced by games on the page
     */
    private Set<Long> allAuthorsIdsOnPage(Page<GamesEntity> gamesPage) {
        return gamesPage.getContent().stream()
                .map(GamesEntity::getAuthorId)
                .collect(Collectors.toSet());
    }

    /**
     * Checks whether the specified user is game owner or admin.
     *
     * @param gameId - id of game
     * @param userId - id of user
     * @Author: Artemii Gorelov
     */
    private void checkOwnerOrAdmin(Long gameId, Long userId) {
        gamesServiceLogger.info("Called checkOwnerOrAdmin games service method");
        GamesEntity game = gamesRepository.findById(gameId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found"));

        UserEntity user = usersRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        if (!game.getAuthorId().equals(userId) && user.getRole() != UserRole.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to modify this game");
        }
    }
}
