package gde.gde_website.games.service;

import gde.gde_website.games.entity.CommentEntity;
import gde.gde_website.games.mapper.GamesCommentsMapper;
import gde.gde_website.games.model.GamesCommentResponse;
import gde.gde_website.games.repository.CommentRepository;
import gde.gde_website.games.repository.GamesRepository;
import gde.gde_website.users.entity.UserEntity;
import gde.gde_website.users.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GamesCommentsService {
    private static final Logger commentsServiceLogger = LoggerFactory.getLogger(GamesCommentsService.class);

    private final CommentRepository commentRepository;
    private final GamesRepository gamesRepository;
    private final UsersRepository usersRepository;
    private final GamesCommentsMapper gamesCommentsMapper;

    @Transactional(readOnly = true)
    public Page<GamesCommentResponse> getAllCommentsByGameId(Long gameId, Pageable pageable) {
        commentsServiceLogger.info("Called getAllCommentsByGameId method");

        gamesRepository.findById(gameId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Page<CommentEntity> commentEntityPage = commentRepository.findAllByGameIdOrderByCreatedAtDesc(gameId, pageable);

        Map<Long, UserEntity> authorsMap = usersRepository.findAllById(allAuthorsIdsOnPage(commentEntityPage))
                .stream().collect(Collectors.toMap(UserEntity::getId, userEntity -> userEntity));

        return commentEntityPage.map(commentEntity ->
                gamesCommentsMapper.commentEntityToCommentResponse(commentEntity, authorsMap));
    }

    private Set<Long> allAuthorsIdsOnPage(Page<CommentEntity> commentsPage) {
        return commentsPage.getContent().stream()
                .map(CommentEntity::getUserId)
                .collect(Collectors.toSet());
    }
}
