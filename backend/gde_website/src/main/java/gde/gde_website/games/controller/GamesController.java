package gde.gde_website.games.controller;

import gde.gde_website.games.entity.GamesEntity;
import gde.gde_website.games.model.Games;
import gde.gde_website.games.model.GamesResponce;
import gde.gde_website.games.repository.GamesRepository;
import gde.gde_website.games.service.GamesService;
import gde.gde_website.security.JwtUtils;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/games")
@RequiredArgsConstructor
public class GamesController {
    private static final Logger gamesControllerLogger = LoggerFactory.getLogger(GamesController.class);

    private final GamesService gamesService;
    private final JwtUtils jwtUtils; // #TODO: debug

    @GetMapping
    public ResponseEntity<Page<GamesEntity>> getAllGames(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "24") int size
    ) {
        gamesControllerLogger.info("Called getAllGames endpoint");
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt"));
        return ResponseEntity.status(HttpStatus.OK).body(gamesService.getAllGames(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GamesResponce> getGameById(
            @PathVariable("id") Long id,
            Authentication authentication) {
        gamesControllerLogger.info("Called getGameById endpoint");
        Long currentUserId = null;

        if (authentication != null && authentication.isAuthenticated()) {
            currentUserId = (Long) authentication.getPrincipal();
        }

        return ResponseEntity.status(HttpStatus.OK).body(gamesService.getGameById(id, currentUserId));
    }

    @PostMapping
    public ResponseEntity<Games> createGame(
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam String bannerUrl,
            Authentication authentication
    ) {
        gamesControllerLogger.info("Called createGame endpoint");
        if (authentication == null || !authentication.isAuthenticated()) {
            gamesControllerLogger.error("User create game permissions error");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        }

        Long currentUserId = (Long) authentication.getPrincipal();

        Games gameWithCurrentAuthor = new Games(
                null,
                currentUserId,
                title,
                description,
                bannerUrl,
                null,
                null
        );

        Games createGame = gamesService.createGame(gameWithCurrentAuthor, currentUserId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createGame);
    }

    // #TODO: implement this function
    // This function must update game info with specific requested id
    @PatchMapping("/{id}")
    public ResponseEntity<Games> updateGame(@PathVariable("id") Long id) {
        return null;
    }

    // #TODO: implement this function
    // This function must delete game with specific requested id
    @DeleteMapping("/{id}")
    public ResponseEntity<Games> deleteGame(
            @PathVariable("id") Long id,
            Authentication authentication
    ) {
        gamesControllerLogger.info("Called deleteGame endpoint");
        if (authentication == null || !authentication.isAuthenticated()) {
            gamesControllerLogger.error("User delete game permissions error");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        }

        Long currentUserId = (Long) authentication.getPrincipal();

        Games deletedGame = gamesService.deleteGame(id, currentUserId);
        return ResponseEntity.status(204).body(deletedGame);
    }

    // #TODO: debug
    // Testing Method
    @GetMapping("/dev/token/{userId}")
    public String getTestToken(@PathVariable Long userId) {
        return jwtUtils.generateToken(userId);
    }
}
