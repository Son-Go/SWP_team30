package gde.gde_website.games.service;

import gde.gde_website.games.entity.GamesEntity;
import gde.gde_website.games.repository.GamesRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class GamesService {

    private final GamesRepository repository;

    public GamesService(GamesRepository repository) {
        this.repository = repository;
    }

    public Page<GamesEntity> getAllGames(Pageable pageable) {
        return repository.findAllByOrderByCreatedAtDesc(pageable);
    }
}
