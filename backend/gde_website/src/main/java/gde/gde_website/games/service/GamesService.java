package gde.gde_website.games.service;

import gde.gde_website.games.entity.GamesEntity;
import gde.gde_website.games.entity.GamesScreenshotEntity;
import gde.gde_website.games.entity.TagEntity;
import gde.gde_website.games.mapper.GamesMapper;
import gde.gde_website.games.model.*;
import gde.gde_website.games.repository.GameScreenshotsRepository;
import gde.gde_website.games.repository.GameTagRepository;
import gde.gde_website.games.repository.GamesRepository;
import gde.gde_website.games.repository.TagRepository;
import gde.gde_website.users.entity.UserEntity;
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
     * This method is used for getting list of all games divided on the groups of specific size request
     * @param pageable - page request
     * @return - returns  sublist of games entity
     * @Author: Artemii Gorelov, Egor Grishin
     */
    @Transactional(readOnly = true)
    public Page<GamesPageResponce> getAllGames(Pageable pageable) {
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

                    return new GamesPageResponce(
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
    public Page<GamesPageResponce> getGamesByTags(List<String> tags, Pageable pageable) {
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

                    return new GamesPageResponce(
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
    public GamesCardResponce getGameById(Long gameId, Long currentUserId) {
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
}
