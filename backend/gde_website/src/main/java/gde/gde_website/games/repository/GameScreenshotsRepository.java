package gde.gde_website.games.repository;

import gde.gde_website.games.entity.GamesScreenshotEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for working with game screenshots links
 * @Author: Artemii Gorelov
 */
@Repository
public interface GameScreenshotsRepository extends JpaRepository<GamesScreenshotEntity, Long> {
    /**
     * This function is used to find all game screenshots links for game with requested id
     * @param gameId - id of requested game
     * @return - list of screenshots links
     */
    List<GamesScreenshotEntity> findAllByGameId(Long gameId);

    /**
     * This function is used for quick deleting of all screenshots links
     * @param gameId - id of requested game
     */
    void deleteAllByGameId(Long gameId);
}
