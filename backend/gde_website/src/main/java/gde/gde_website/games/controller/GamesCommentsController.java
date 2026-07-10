package gde.gde_website.games.controller;

import gde.gde_website.games.model.GamesCommentResponse;
import gde.gde_website.games.service.GamesCommentsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
}
