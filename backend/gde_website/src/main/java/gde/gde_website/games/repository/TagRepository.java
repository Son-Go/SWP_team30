package gde.gde_website.games.repository;

import gde.gde_website.games.entity.TagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for working with tag records.
 *
 * @Author: Egor Grishin
 */
@Repository
public interface TagRepository extends JpaRepository<TagEntity, Integer> {
    /**
     * Finds a tag by its unique name.
     *
     * @param name - tag name
     * @return optional tag entity with the provided name
     */
    Optional<TagEntity> findByName(String name);
}
