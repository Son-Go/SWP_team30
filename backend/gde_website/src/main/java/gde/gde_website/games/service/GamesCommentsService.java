package gde.gde_website.games.service;

import gde.gde_website.games.entity.CommentEntity;
import gde.gde_website.games.mapper.GamesCommentsMapper;
import gde.gde_website.games.model.GamesCommentResponse;
import gde.gde_website.games.model.GamesCreateCommentRequest;
import gde.gde_website.games.repository.CommentRepository;
import gde.gde_website.games.repository.GamesRepository;
import gde.gde_website.users.entity.UserEntity;
import gde.gde_website.users.model.UserRole;
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
import org.springframework.data.util.Pair;

import java.util.Map;
import java.util.Objects;
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

    @Transactional
    public GamesCommentResponse createComment(GamesCreateCommentRequest request, Long authorId, Long gameId) {
        commentsServiceLogger.info("Called createComment method");

        gamesRepository.findById(gameId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        UserEntity author = usersRepository.findById(authorId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        CommentEntity comment = new CommentEntity(
                authorId,
                gameId,
                request.text()
        );

        CommentEntity savedComment = commentRepository.save(comment);

        commentsServiceLogger.info("Successfully created comment id={}", savedComment.getId());

        return gamesCommentsMapper.commentEntityToCommentResponse(savedComment, author);
    }

    @Transactional
    public GamesCommentResponse updateComment(GamesCreateCommentRequest request,
                                              Long currentUserId,
                                              Long commentId,
                                              Long gameId
    ) {
        commentsServiceLogger.info("Called updateComment method");

        Pair<CommentEntity, UserEntity> afterCheck = checkPermissions(commentId, currentUserId, gameId);

        CommentEntity commentToUpdate = afterCheck.getFirst();

        UserEntity author = afterCheck.getSecond();

        commentToUpdate.setText(request.text());

        CommentEntity savedComment = commentRepository.save(commentToUpdate);

        commentsServiceLogger.info("Successfully update comment id={}", savedComment.getId());

        return gamesCommentsMapper.commentEntityToCommentResponse(savedComment, author);
    }

    @Transactional
    public GamesCommentResponse deleteComment(Long currentUserId,
                                              Long commentId,
                                              Long gameId
    ) {
        commentsServiceLogger.info("Called deleteComment method");

        Pair<CommentEntity, UserEntity> afterCheck = checkPermissions(commentId, currentUserId, gameId);

        CommentEntity commentToDelete = afterCheck.getFirst();

        UserEntity author = afterCheck.getSecond();

        commentRepository.delete(commentToDelete);

        commentsServiceLogger.info("Successfully delete comment id={}", commentToDelete.getId());

        return gamesCommentsMapper.commentEntityToCommentResponse(commentToDelete, author);
    }

    private Set<Long> allAuthorsIdsOnPage(Page<CommentEntity> commentsPage) {
        return commentsPage.getContent().stream()
                .map(CommentEntity::getUserId)
                .collect(Collectors.toSet());
    }

    /**
     * Checks whether the specified user is game owner or admin.
     *
     * @param commentId - id of comment
     * @param userId - id of user
     * @Author: Egor Grishin
     */
    private Pair<CommentEntity, UserEntity> checkPermissions(Long commentId, Long userId, Long gameId) {
        commentsServiceLogger.info("Called checkPermissions comments service method");

        CommentEntity comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found"));

        if (!Objects.equals(comment.getGameId(), gameId)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Comment id=" + commentId + " does not belong to game id=" + gameId
            );
        }

        UserEntity user = usersRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        UserEntity author = usersRepository.findById(comment.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Author not found"));

        if (!comment.getUserId().equals(userId) && user.getRole() != UserRole.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to modify this comment");
        }

        return Pair.of(comment, author);
    }
}
