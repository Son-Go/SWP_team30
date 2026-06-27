package gde.gde_website.games.repository;

import gde.gde_website.games.entity.GamesEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for working with games table.
 *
 * @Author: Artemii Gorelov
 */
@Repository
public interface GamesRepository extends JpaRepository<GamesEntity, Long> {
    /**
     * Returns a page of games ordered by creation date descending.
     * Related tags are loaded together with games.
     *
     * @param pageable - pagination information
     * @return sublist of game entities sorted by creation date descending
     * @Author: Artemii Gorelov, Egor Grishin
     */
    @EntityGraph(attributePaths = {"gameTags", "gameTags.tag"})
    Page<GamesEntity> findAllByOrderByCreatedAtDesc(Pageable pageable);

    /**
     * Finds a game by id and loads related tags together with it.
     * This method is used for building detailed game card response.
     *
     * @param id - game id
     * @return optional game entity with loaded tag relations
     */
    @EntityGraph(attributePaths = {"gameTags", "gameTags.tag"})
    Optional<GamesEntity> findDetailedById(Long id);

    /**
     * Returns games that have at least one tag from the provided list.
     * Related tags are loaded together with matching games.
     *
     * @param tagNames - list of tag names used for filtering
     * @param pageable - pagination information
     * @return page of games matching at least one provided tag
     */
    @Query("SELECT DISTINCT g FROM GamesEntity g " +
            "JOIN g.gameTags gt " +
            "JOIN gt.tag t " +
            "WHERE t.name IN :tagNames " + "ORDER BY g.createdAt DESC")
    @EntityGraph(attributePaths = {"gameTags", "gameTags.tag"})
    Page<GamesEntity> findByTagNames(List<String> tagNames, Pageable pageable);
}
