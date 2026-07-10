package gde.gde_website.games.repository;

import gde.gde_website.games.entity.CommentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
    Page<CommentEntity> findAllByGameIdOrderByCreatedAtDesc(Long gameId, Pageable pageable);
}
