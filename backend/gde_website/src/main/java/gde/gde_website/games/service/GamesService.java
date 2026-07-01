package gde.gde_website.games.service;

import gde.gde_website.games.entity.GamesEntity;
import gde.gde_website.games.entity.GamesScreenshotEntity;
import gde.gde_website.games.entity.TagEntity;
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

import java.util.List;
import java.util.Map;

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

    /**
     * This method is used for mapping one game
     * @param game - game card
     * @param authorsMap - map of authors instead of database requests
     * @return - new games page response
     * @Author: Artemii Gorelov
     */
    private GamesPageResponse mapToResponse(GamesEntity game, Map<Long, UserEntity> authorsMap) {
        List<String> tagNames = game.getGameTags().stream()
                .map(gameTag -> gameTag.getTag().getName()).toList();

        UserEntity author = authorsMap.get(game.getAuthorId());

        AuthorResponse authorResp = null;
        if (author != null) {
            authorResp = new AuthorResponse(
                    author.getUsername(),
                    author.getProfileImageUrl(),
                    author.getEmail()
            );
        }

        return new GamesPageResponse(
                game.getId(),
                game.getAuthorId(),
                game.getTitle(),
                game.getDescription(),
                game.getBannerUrl(),
                authorResp,
                tagNames
        );
    }

    /**
     * This method is used for getting list of all games divided on the groups of specific size request
     * @param pageable - page request
     * @return - returns  sublist of games entity
     * @Author: Artemii Gorelov, Egor Grishin
     */
    @Transactional(readOnly = true)
    public Page<GamesPageResponse> getAllGames(Pageable pageable) {
        gamesServiceLogger.info("Called getAllGames method");
        return gamesRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(game -> {
                    List<String> tagNames = game.getGameTags().stream()
                            .map(gameTag -> gameTag.getTag().getName()).toList();

                    UserEntity author = usersRepository.findById(game.getAuthorId()).orElse(null);

                    AuthorResponse authorResp = null;
                    if (author != null) {
                        authorResp = new AuthorResponse(
                                author.getUsername(),
                                author.getProfileImageUrl(),
                                author.getEmail()
                        );
                    }

                    return new GamesPageResponse(
                            game.getId(),
                            game.getAuthorId(),
                            game.getTitle(),
                            game.getDescription(),
                            game.getBannerUrl(),
                            authorResp,
                            tagNames
                    );
                });
    }

    /**
     * This method is used for getting list of games filtered by specific tags
     * @param tags - list of tag names to filter game by (uses OR logic - game must have at least one of the tags)
     * @param pageable - pagination information including page number, size and sort order,
     * @return paginated list of games that match at least one of the specified tags, or all games if tags list is null or empty
     * @Author: Artemii Gorelov, Egor Grishin
     */
    @Transactional(readOnly = true)
    public Page<GamesPageResponse> getGamesByTags(List<String> tags, Pageable pageable) {
        gamesServiceLogger.info("Called getAllGames method with tags {}", tags);

        if (tags == null || tags.isEmpty()) {
            return getAllGames(pageable);
        }

        return gamesRepository.findByTagNames(tags, pageable)
                .map(game -> {
                    List<String> tagNames = game.getGameTags().stream()
                            .map(gameTag -> gameTag.getTag().getName()).toList();

                    UserEntity author = usersRepository.findById(game.getAuthorId()).orElse(null);

                    AuthorResponse authorResp = null;
                    if (author != null) {
                        authorResp = new AuthorResponse(
                                author.getUsername(),
                                author.getProfileImageUrl(),
                                author.getEmail()
                        );
                    }

                    return new GamesPageResponse(
                            game.getId(),
                            game.getAuthorId(),
                            game.getTitle(),
                            game.getDescription(),
                            game.getBannerUrl(),
                            authorResp,
                            tagNames
                    );
                });
    }

    /**
     * This function is used for getting game by requested id
     * @param gameId - id of the game to get
     * @param currentUserId - user id
     * @return game response object
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

        return mapper.entityToResponse(game, currentUserId, authorResponse, screenshots);
    }

    /**
     * This function is used to create new game in database
     * @param authorId - id of author that creating game
     * @return new games object
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
     * This function is used for updating game with requested id
     * @param gameId - id of game to be updated
     * @return updated game object
     * @throws ResponseStatusException with codes:
     * 404 when id of game to be created does not found inside database
     * 401 when user who wants to update game is not its author or admin
     * @Author: Egor Grishin
     */
    @Transactional
    public Games updateGame(UpdateGameRequest request, Long currentUserId, Long gameId) {
        gamesServiceLogger.info("Called GamesService updateGame method");

        checkOwnerOrAdmin(gameId, currentUserId);

        GamesEntity gameToUpdate = gamesRepository.findById(gameId).
                orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (!gameToUpdate.getAuthorId().equals(currentUserId)) {
            gamesServiceLogger.error("User permissions error");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not the owner of this game");
        }

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
     * This method is used for deleting game with requested id
     * @param gameId - id of game to be deleted
     * @param currentUserId - id of user who wants to delete game
     * @return new object of deleted game
     * @throws ResponseStatusException with codes:
     * 404 when game with requested id does not found
     * 401 when user who wants to delete the game is not game author or admin
     * @Author: Artemii Gorelov, Egor Grishin
     */
    @Transactional
    public Games deleteGame(Long gameId, Long currentUserId) {
        gamesServiceLogger.info("Called GamesService deleteGame method");

        checkOwnerOrAdmin(gameId, currentUserId);

        GamesEntity gameToDelete = gamesRepository.findById(gameId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found"));

        if (!gameToDelete.getAuthorId().equals(currentUserId)) {
            gamesServiceLogger.error("User permissions error");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not the owner of this game");
        }

        gamesRepository.delete(gameToDelete);

        gamesServiceLogger.info("Successfully deleted game id={}", gameId);
        return mapper.entityToGames(gameToDelete);
    }

    /**
     * This function is used for getting author info by requested specific id
     * @param id - author id, we want info about
     * @return returns object of AuthorResponse which contains author username, profile image, and email
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
     * This function is used for returning list of all tags
     * @return new TagsResponse Object which contains list of tags
     * @Author: Egor Grishin
     */
    public TagsResponse getAllTags() {
        List<String> gameTags = tagRepository.findAll().stream().
                map(TagEntity::getName).toList();

        return new TagsResponse(gameTags);
    }

    /**
     * This method is used for admin to approving games
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
     * This method is used for admin to rejecting games
     * @param gameId - if of game to be rejected
     * @param adminId - id of admin who is rejecting
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
     * This method is used for updating game description by admin
     * @param gameId - id of game whoos description would be changed
     * @param description - new description
     * @param adminId - id of admin who is changing description of the game
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
     * This method is used for deleting all games from author after author was deleted
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
     * This method is used for checking is user have permissions to update or delete games
     * @param gameId - requested game id
     * @param userId - requested user id
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
