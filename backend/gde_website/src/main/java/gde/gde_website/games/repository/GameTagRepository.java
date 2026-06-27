package gde.gde_website.games.repository;

import gde.gde_website.games.entity.GameTagEntity;
import gde.gde_website.games.entity.GameTagId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository for working with game-tag relations.
 *
 * @Author: Egor Grishin
 */
@Repository
public interface GameTagRepository extends JpaRepository<GameTagEntity, GameTagId> {
    /**
     * Deletes all tag relations for the specified game.
     *
     * @param gameId - game id whose tag relations should be deleted
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM GameTagEntity g WHERE g.gameId = :gameId")
    void deleteAllByGameId(@Param("gameId") Long gameId);

    /**
     * Inserts a new relation between game and tag directly into {@code game_tag} table.
     *
     * @param gameId - game id
     * @param tagId - tag id
     */
    @Modifying
    @Query(value = "INSERT INTO game_tag (game_id, tag_id) VALUES (:gameId, :tagId)", nativeQuery = true)
    void insertGameTag(@Param("gameId") Long gameId, @Param("tagId") Integer tagId);
}
