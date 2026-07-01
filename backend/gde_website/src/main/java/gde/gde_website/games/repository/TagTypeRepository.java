package gde.gde_website.games.repository;

import gde.gde_website.games.entity.TagTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TagTypeRepository extends JpaRepository<TagTypeEntity, Integer> {
    Optional<TagTypeEntity> findByType(String type);
}
