package gde.gde_website.games.service;

import gde.gde_website.games.entity.GamesEntity;
import gde.gde_website.games.mapper.GamesMapper;
import gde.gde_website.games.model.Games;
import gde.gde_website.games.model.GamesResponce;
import gde.gde_website.games.repository.GamesRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * This class is implementing business logic of requests handlers
 * @Author: Artemii Gorelov, Egor Grishin
 */
@Service
@RequiredArgsConstructor
public class GamesService {
    private static final Logger gamesServiceLogger = LoggerFactory.getLogger(GamesService.class);

    private final GamesRepository repository;
    private final GamesMapper mapper;

    /**
     * This method is used for getting list of all games divided on the groups of specific size request
     * @param pageable - page request
     * @return - returns  sublist of games entity
     * @Author: Artemii Gorelov
     */
    public Page<GamesEntity> getAllGames(Pageable pageable) {
        gamesServiceLogger.info("Called getAllGames method");
        return repository.findAllByOrderByCreatedAtDesc(pageable);
    }

    /**
     * This function is used for getting game by requested id
     * @param gameId - id of the game to get
     * @param currentUserId - user id
     * @return game response object
     * @Author: Egor Grishin
     */
    public GamesResponce getGameById(Long gameId, Long currentUserId) {
        gamesServiceLogger.info("Called GamesService getGameById method");
        GamesEntity game = repository.findById(gameId).
                orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return mapper.entityToResponse(game, currentUserId);
    }

    /**
     * This function is used to create new game in database
     * @param entity - entity of game to be created
     * @param authorId - id of author that creating game
     * @return new games object
     * @Author: Artemii Gorelov
     */
    public Games createGame(Games entity, Long authorId) {
        gamesServiceLogger.info("Called GamesService createGame method");
        GamesEntity game = new GamesEntity(
                authorId,
                entity.title(),
                entity.description(),
                entity.bannerUrl()
        );

        GamesEntity savedGame = repository.save(game);
        return mapper.entityToGames(savedGame);
    }

    /**
     * This function is used for updating game with requested id
     * @param entity - entity of game to be updated
     * @param gameId - id of game to be updated
     * @return updated game object
     * @throws ResponseStatusException with codes:
     * 404 when id of game to be created does not found inside database
     * 401 when user who wants to update game is not its author or admin
     * @Author: Egor Grishin
     */
    public Games updateGame(Games entity, Long gameId) {
        gamesServiceLogger.info("Called GamesService updateGame method");
        GamesEntity gameToUpdate = repository.findById(gameId).
                orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (!gameToUpdate.getAuthorId().equals(entity.authorId())) {
            gamesServiceLogger.error("User permissions error");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not the owner of this game");
        }

        gameToUpdate.setTitle(entity.title());
        gameToUpdate.setDescription(entity.description());
        gameToUpdate.setBannerUrl(entity.bannerUrl());

        GamesEntity savedGame = repository.save(gameToUpdate);
        gamesServiceLogger.info("Successfully updated game id={}", gameId);

        return mapper.entityToGames(savedGame);
    }

    /**
     * This method is used for deleting game with requested id
     * @param gameId - id of game to be deleted
     * @param currentUserId - id of user who wants to delete game
     * @return new object of deleted game
     * @throws ResponseStatusException with codes:
     * 404 when game with requested id does not found
     * 401 when user who wants to delete the game is not game author or admin
     * @Author: Artemii Gorelov
     */
    public Games deleteGame(Long gameId, Long currentUserId) {
        gamesServiceLogger.info("Called GamesService deleteGame method");
        GamesEntity entity = repository.findById(gameId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found"));

        if (!entity.getAuthorId().equals(currentUserId)) {
            gamesServiceLogger.error("User permissions error");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not the owner of this game");
        }

        repository.delete(entity);
        gamesServiceLogger.info("Successfully deleted game id={}", gameId);
        return mapper.entityToGames(entity);
    }
}
