package gde.gde_website.games.repository;

import gde.gde_website.games.entity.GameTagEntity;
import gde.gde_website.games.entity.GameTagId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface GameTagRepository extends JpaRepository<GameTagEntity, GameTagId> {
    @Modifying
    @Query("DELETE FROM GameTagEntity g WHERE g.gameId = :gameId")
    void deleteAllByGameId(Long gameId);
}
