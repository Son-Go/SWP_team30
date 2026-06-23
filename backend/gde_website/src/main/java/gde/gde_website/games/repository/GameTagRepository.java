package gde.gde_website.games.repository;

import gde.gde_website.games.entity.GameTagEntity;
import gde.gde_website.games.entity.GameTagId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameTagRepository extends JpaRepository<GameTagEntity, GameTagId> {
}
