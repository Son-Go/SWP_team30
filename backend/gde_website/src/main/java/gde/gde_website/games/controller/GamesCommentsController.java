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

@RestController
@RequestMapping("/games/{game_id}/comments")
@RequiredArgsConstructor
public class GamesCommentsController {
    private static final Logger gamesCommentsControllerLogger = LoggerFactory.getLogger(GamesCommentsController.class);

    private final GamesCommentsService gamesCommentsService;

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

    private void checkAuth(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            gamesCommentsControllerLogger.error("User create comment permissions error");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        }
    }
}
