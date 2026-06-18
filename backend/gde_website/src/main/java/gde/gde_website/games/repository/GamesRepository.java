package gde.gde_website.games.repository;

import gde.gde_website.games.entity.GamesEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface GamesRepository extends JpaRepository<GamesEntity, Long> {
    Page<GamesEntity> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
