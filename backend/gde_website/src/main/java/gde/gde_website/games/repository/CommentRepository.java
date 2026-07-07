package gde.gde_website.games.repository;

import gde.gde_website.games.entity.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
    List<CommentEntity> findAllByGameIdOrderByCreatedAtDesc(Long gameId);
}
