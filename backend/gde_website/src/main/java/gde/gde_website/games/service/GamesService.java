package gde.gde_website.games.service;

import gde.gde_website.games.controller.GamesController;
import gde.gde_website.games.entity.GamesEntity;
import gde.gde_website.games.mapper.GamesMapper;
import gde.gde_website.games.model.Games;
import gde.gde_website.games.model.GamesResponce;
import gde.gde_website.games.repository.GamesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class GamesService {
    private static final Logger gamesServiceLogger = LoggerFactory.getLogger(GamesService.class);

    private final GamesRepository repository;
    private final GamesMapper mapper;

    public GamesService(GamesRepository repository, GamesMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public Page<GamesEntity> getAllGames(Pageable pageable) {
        gamesServiceLogger.info("Called getAllGames method");
        return repository.findAllByOrderByCreatedAtDesc(pageable);
    }

    public GamesResponce getGameById(Long gameId, Long currentUserId) {
        gamesServiceLogger.info("Called getGameById method");
        GamesEntity game = repository.findById(gameId).
                orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return mapper.entityToResponse(game, currentUserId);
    }

    public Games createGame(Games entity, Long authorId) {
        gamesServiceLogger.info("Called createGame method");
        GamesEntity game = new GamesEntity(
                authorId,
                entity.title(),
                entity.description(),
                entity.bannerUrl()
        );

        GamesEntity savedGame = repository.save(game);
        gamesServiceLogger.info("Successfully created game");
        return mapper.entityToGames(savedGame);
    }

    public Games deleteGame(Long id, Long currentUserId) {
        gamesServiceLogger.info("Called deleteGame method");
        GamesEntity entity = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found"));

        if (!entity.getAuthorId().equals(currentUserId)) {
            gamesServiceLogger.error("User permissions error");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not the owner of this game");
        }

        repository.delete(entity);
        gamesServiceLogger.info("Successfully deleted game");
        return mapper.entityToGames(entity);
    }
}
