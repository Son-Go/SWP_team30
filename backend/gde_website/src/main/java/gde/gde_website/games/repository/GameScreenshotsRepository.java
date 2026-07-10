package gde.gde_website.games.repository;

import gde.gde_website.games.entity.GamesScreenshotEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

/**
 * Repository for working with stored game screenshot records.
 * Each record contains a media URL and a flag indicating whether it belongs
 * to the {@code videos} or {@code pictures} response group.
 * @Author: Artemii Gorelov
 */
@Repository
public interface GameScreenshotsRepository extends JpaRepository<GamesScreenshotEntity, Long> {
    /**
     * Loads all stored screenshot records for the requested game.
     *
     * @param gameId - id of requested game
     * @return list of screenshot entities for the game
     */
    List<GamesScreenshotEntity> findAllByGameId(Long gameId);

    /**
     * Loads all non-video screenshot records for the requested set of games.
     * Returns only entries where {@code isVideo = false}.
     *
     * @param gameIds - ids of requested games
     * @return list of picture screenshot entities for the requested games
     */
    List<GamesScreenshotEntity> findAllByGameIdInAndIsVideoFalse(Collection<Long> gameIds);

    /**
     * Deletes all stored screenshot records for the requested game.
     *
     * @param gameId - id of requested game
     */
    void deleteAllByGameId(Long gameId);
}
