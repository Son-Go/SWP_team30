package gde.gde_website.games.service;

import gde.gde_website.games.entity.GamesEntity;
import gde.gde_website.games.mapper.GamesMapper;
import gde.gde_website.games.model.Games;
import gde.gde_website.games.model.GamesResponce;
import gde.gde_website.games.repository.GamesRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class GamesService {

    private final GamesRepository repository;
    private final GamesMapper mapper;

    public GamesService(GamesRepository repository, GamesMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public Page<GamesEntity> getAllGames(Pageable pageable) {
        return repository.findAllByOrderByCreatedAtDesc(pageable);
    }

    public GamesResponce getGameById(Long gameId, Long currentUserId) {
        GamesEntity game = repository.findById(gameId).
                orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return mapper.entityToResponse(game, currentUserId);
    }

    public Games createGame(Games entity, Long authorId) {
        GamesEntity game = new GamesEntity(
                authorId,
                entity.title(),
                entity.description(),
                entity.bannerUrl()
        );

        GamesEntity savedGame = repository.save(game);
        return mapper.entityToGames(savedGame);
    }
}
