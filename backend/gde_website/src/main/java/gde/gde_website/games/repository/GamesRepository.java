package gde.gde_website.games.repository;

import gde.gde_website.games.entity.GamesEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * This class is used for working with PostgreSQL database connected with games table
 * @Author: Artemii Gorelov
 */
@Repository
public interface GamesRepository extends JpaRepository<GamesEntity, Long> {
    /**
     * This method is used for sorting all game object by groups of specific size
     * @param pageable - pagination information
     * @return returns sublist of games entity sorted by date of creation
     * @Author: Artemii Gorelov, Egor Grishin
     */
    @EntityGraph(attributePaths = {"gameTags", "gameTags.tag"})
    Page<GamesEntity> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
