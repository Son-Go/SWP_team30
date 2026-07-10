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

/**
 * Service responsible for reading and mutating game comments.
 *
 * Supports paginated comment listing for a game together with create, update and delete operations.
 * Mutating operations validate game existence, authenticated user existence and ownership/admin
 * permissions according to the current nested route contract.
 *
 * @Author: Egor Grishin
 */
@Service
@RequiredArgsConstructor
public class GamesCommentsService {
    private static final Logger commentsServiceLogger = LoggerFactory.getLogger(GamesCommentsService.class);

    private final CommentRepository commentRepository;
    private final GamesRepository gamesRepository;
    private final UsersRepository usersRepository;
    private final GamesCommentsMapper gamesCommentsMapper;

    /**
     * Returns a page of comments for the specified game ordered by creation time descending.
     * Before loading comments, validates that the requested parent game exists.
     * Comment authors for the current page are loaded in batch and reused during DTO mapping.
     *
     * @param gameId - id of the game whose comments should be returned
     * @param pageable - page request for comments list
     * @return paginated comments response for the requested game
     * @throws ResponseStatusException with code {@code 404} when game does not exist
     * @Author: Egor Grishin
     */
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

    /**
     * Creates a new comment for the specified game using text from the validated request.
     * The method checks that both parent game and authenticated author exist before persisting.
     *
     * @param request - validated create comment request
     * @param authorId - id of authenticated user who creates the comment
     * @param gameId - id of game that owns the comment
     * @return created comment response containing author and timestamps
     * @throws ResponseStatusException with code {@code 404} when game does not exist
     * @throws ResponseStatusException with code {@code 401} when author does not exist
     * @Author: Egor Grishin
     */
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

    /**
     * Updates text of an existing comment after validating nested route ownership and permissions.
     * The response preserves original comment author information even when the update is performed by admin.
     *
     * @param request - validated update comment request
     * @param currentUserId - id of authenticated user performing update
     * @param commentId - id of comment to update
     * @param gameId - id of parent game used to validate nested route ownership
     * @return updated comment response
     * @Author: Egor Grishin
     */
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

    /**
     * Deletes an existing comment after validating nested route ownership and permissions.
     * The current implementation returns deleted comment data mapped with original author information.
     *
     * @param currentUserId - id of authenticated user performing deletion
     * @param commentId - id of comment to delete
     * @param gameId - id of parent game used to validate nested route ownership
     * @return deleted comment response according to current controller/service contract
     * @Author: Egor Grishin
     */
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

    /**
     * Extracts unique author ids for comments contained in the current page.
     * Used to batch-load comment authors and avoid per-comment user lookup during mapping.
     *
     * @param commentsPage - page of comments
     * @return set of unique comment author ids referenced by the current page
     */
    private Set<Long> allAuthorsIdsOnPage(Page<CommentEntity> commentsPage) {
        return commentsPage.getContent().stream()
                .map(CommentEntity::getUserId)
                .collect(Collectors.toSet());
    }

    /**
     * Validates access to a nested comment resource and returns data needed by mutation methods.
     * The method checks that comment exists, belongs to the provided game id from route,
     * authenticated user exists, original comment author exists, and the acting user is either
     * comment owner or admin.
     *
     * @param commentId - id of comment from route
     * @param userId - id of authenticated user performing operation
     * @param gameId - id of parent game from nested route
     * @return pair where first value is comment entity and second value is original comment author
     * @throws ResponseStatusException with codes:
     * 404 when comment does not exist
     * 400 when comment does not belong to provided game id
     * 401 when acting user or original author does not exist
     * 403 when acting user is neither comment owner nor admin
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
