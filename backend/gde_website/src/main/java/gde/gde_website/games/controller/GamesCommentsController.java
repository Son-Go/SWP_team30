package gde.gde_website.games.controller;

import gde.gde_website.games.model.GamesCommentResponse;
import gde.gde_website.games.model.GamesCreateCommentRequest;
import gde.gde_website.games.service.GamesCommentsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.server.ResponseStatusException;

/**
 * Controller for handling nested comment endpoints under a game.
 * Provides paginated comment listing together with create, update and delete operations.
 *
 * Path variable {@code game_id} is used as the owning game identifier for all operations.
 * For update and delete requests, both {@code game_id} and {@code comment_id} are passed
 * to the service so the request is validated against the nested route contract.
 *
 * @Author: Egor Grishin
 */
@RestController
@RequestMapping("/games/{game_id}/comments")
@RequiredArgsConstructor
public class GamesCommentsController {
    private static final Logger gamesCommentsControllerLogger = LoggerFactory.getLogger(GamesCommentsController.class);

    private final GamesCommentsService gamesCommentsService;

    /**
     * Returns a page of comments for the requested game ordered by creation date descending.
     * The endpoint validates that the parent game exists before loading comments.
     *
     * @param gameId - id of the game whose comments should be returned
     * @param page - zero-based comments page number
     * @param size - number of comments on the page
     * @return HTTP status {@code 200} with paginated comments in response body
     * @Author: Egor Grishin
     */
    @GetMapping
    public ResponseEntity<Page<GamesCommentResponse>> getAllCommentsByGameId(
            @PathVariable("game_id") Long gameId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        gamesCommentsControllerLogger.info("Called GamesCommentsController /games/{game_id}/comments method (get)");

        Pageable pageable = PageRequest.of(page, size);

        return ResponseEntity.status(HttpStatus.OK).body(gamesCommentsService.getAllCommentsByGameId(gameId, pageable));
    }

    /**
     * Creates a new comment for the requested game on behalf of the authenticated user.
     * Request body is validated before the service is called.
     *
     * @param gameId - id of the game for which comment should be created
     * @param request - create comment request containing validated text
     * @param authentication - authenticated user token
     * @return HTTP status {@code 201} with created comment in response body
     * @throws ResponseStatusException with code {@code 401} if user is not authenticated
     * @Author: Egor Grishin
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GamesCommentResponse> createComment(
            @PathVariable("game_id") Long gameId,
            @RequestBody @Valid GamesCreateCommentRequest request,
            Authentication authentication
    ) {
        gamesCommentsControllerLogger.info("Called GamesCommentsController /games/{game_id}/comments method (post)");

        checkAuth(authentication);

        Long currentUserId = (Long) authentication.getPrincipal();

        return ResponseEntity.status(HttpStatus.CREATED).body(gamesCommentsService.createComment(request, currentUserId, gameId));
    }

    /**
     * Updates text of an existing comment identified by nested game and comment ids.
     * Authentication is required, and request body is validated before the service is called.
     *
     * @param commentId - id of comment to update
     * @param request - update request containing validated text
     * @param authentication - authenticated user token
     * @param game_id - id of parent game used to validate nested route ownership
     * @return HTTP status {@code 200} with updated comment in response body
     * @throws ResponseStatusException with code {@code 401} if user is not authenticated
     * @Author: Egor Grishin
     */
    @PatchMapping(path = "/{comment_id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GamesCommentResponse> updateComment(
            @PathVariable("comment_id") Long commentId,
            @RequestBody @Valid GamesCreateCommentRequest request,
            Authentication authentication,
            @PathVariable Long game_id
    ) {
        gamesCommentsControllerLogger
                .info("Called GamesCommentsController /games/{game_id}/comments/{comment_id} method (patch)");

        checkAuth(authentication);

        Long currentUserId = (Long) authentication.getPrincipal();

        return ResponseEntity.status(HttpStatus.OK)
                .body(gamesCommentsService.updateComment(request, currentUserId, commentId, game_id));
    }

    /**
     * Deletes an existing comment identified by nested game and comment ids.
     * Authentication is required before ownership or admin permissions are checked in service layer.
     *
     * @param commentId - id of comment to delete
     * @param authentication - authenticated user token
     * @param game_id - id of parent game used to validate nested route ownership
     * @return HTTP status {@code 204} with deleted comment body returned by current controller contract
     * @throws ResponseStatusException with code {@code 401} if user is not authenticated
     * @Author: Egor Grishin
     */
    @DeleteMapping("/{comment_id}")
    public ResponseEntity<GamesCommentResponse> deleteComment(
            @PathVariable("comment_id") Long commentId,
            Authentication authentication,
            @PathVariable Long game_id
    ) {
        gamesCommentsControllerLogger
                .info("Called GamesCommentsController /games/{game_id}/comments/{comment_id} method (delete)");

        checkAuth(authentication);

        Long currentUserId = (Long) authentication.getPrincipal();

        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(gamesCommentsService.deleteComment(currentUserId, commentId, game_id));
    }

    /**
     * Verifies that request is made by an authenticated user before comment mutation endpoints proceed.
     *
     * @param authentication - current security context authentication
     * @throws ResponseStatusException with code {@code 401} if authentication is absent or invalid
     */
    private void checkAuth(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            gamesCommentsControllerLogger.error("User create comment permissions error");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        }
    }
}
