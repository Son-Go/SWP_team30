package gde.gde_website.games.controller;

import gde.gde_website.games.service.GamesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * This class is used for handling admin HTTP requests for games
 * @Author: Artemii Gorelov
 */
@RestController
@RequestMapping("admin/games")
@RequiredArgsConstructor
public class AdminGamesController {
    private final GamesService gamesService;

    /**
     * This method is used for handling game approving request
     * @param id - id of game
     * @param authentication - authentication token
     * @return returns HTTP status with code {@code 200} when game was successfully approved
     * @Author: Artemii Gorelov
     */
    @PatchMapping("/{id}/approve")
    public ResponseEntity<Void> approve(
            @PathVariable Long id,
            Authentication authentication
    ) {
        gamesService.approveGame(id, (Long) authentication.getPrincipal());
        return ResponseEntity.ok().build();
    }

    /**
     * This method is used for handling rejecting game requests
     * @param id - if of game to be rejected by admin
     * @param authentication - authentication token
     * @return returns HTTP status with code {@code 200} when game was rejected by admin
     * @Author: Artemii Gorelov
     */
    @PatchMapping("/{id}/reject")
    public ResponseEntity<Void> reject(
            @PathVariable Long id,
            Authentication authentication
    ) {
        gamesService.rejectGame(id, (Long) authentication.getPrincipal());
        return ResponseEntity.ok().build();
    }
}
