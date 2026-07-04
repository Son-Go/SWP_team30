package gde.gde_website.games.controller;

import gde.gde_website.games.model.*;
import gde.gde_website.games.service.GamesService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Controller for handling HTTP requests related to games.
 * Provides endpoints for listing games, fetching a game card by id,
 * creating, updating and deleting games, fetching author information,
 * and returning the list of all available tags.
 *
 * @Author: Artemii Gorelov, Egor Grishin
 */
@RestController
@RequestMapping("/games")
@RequiredArgsConstructor
public class GamesController {
    private static final Logger gamesControllerLogger = LoggerFactory.getLogger(GamesController.class);

    private final GamesService gamesService;

    /**
     * This method is used for handling get list of all games or games filtered by tags request,
     * divided on the groups of specific size
     * @param page - initial page
     * @param size - number of elements on each page
     * @param tags - optional list of tag names to filter games by; if not provided or empty, returns all games
     * @return - returns status OK  (code 200) with sublist of games entity inside response body
     * @Author: Artemii Gorelov, Egor Grishin
     */
    @GetMapping
    public ResponseEntity<Page<GamesPageResponse>> getAllGames(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "24") int size,
            @RequestParam(required = false) List<String> tags
    ) {

        gamesControllerLogger.info("Called GamesController /games method (get)");
        Pageable pageable = PageRequest.of(page, size);

        Page<GamesPageResponse> games;
        if (tags != null && !tags.isEmpty()) {
            games = gamesService.getGamesByTags(tags, pageable);
        } else {
            games = gamesService.getAllGames(pageable);
        }

        return ResponseEntity.status(HttpStatus.OK).body(games);
    }

    /**
     * This method is used for handling get game by specific id request
     * @param id - game id
     * @param authentication - user token
     * @return - status OK (code 200) with requested game inside response body
     * @Author: Egor Grishin
     */
    @GetMapping("/{id}")
    public ResponseEntity<GamesCardResponse> getGameById(
            @PathVariable("id") Long id,
            Authentication authentication) {

        gamesControllerLogger.info("Called GamesController /games/id method (get)");
        Long currentUserId = null;

        if (authentication != null && authentication.isAuthenticated()) {
            currentUserId = (Long) authentication.getPrincipal();
        }

        return ResponseEntity.status(HttpStatus.OK).body(gamesService.getGameById(id, currentUserId));
    }

    /**
     * Handles request for creating a new game.
     * Accepts a JSON request body with game fields and an optional list of tag names.
     * The authenticated user becomes the author of the created game.
     *
     * @param request - request body containing title, description, banner url and optional tags
     * @param authentication - authenticated user token
     * @return HTTP status {@code 201} with created game data in response body
     * @throws ResponseStatusException with code {@code 401} if user is not authenticated
     * @Author: Artemii Gorelov
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Games> createGame(
            @RequestBody GamesCreateRequest request,
            Authentication authentication
    ) {

        gamesControllerLogger.info("Called GamesController /games (post)");
        if (authentication == null || !authentication.isAuthenticated()) {
            gamesControllerLogger.error("User create game permissions error");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        }

        Long currentUserId = (Long) authentication.getPrincipal();

        Games createGame = gamesService.createGame(request, currentUserId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createGame);
    }

    /**
     * Handles request for updating an existing game.
     * Accepts a JSON request body. Any non-null field from the request replaces
     * the corresponding game field. When {@code gameTags} is provided, all current
     * game-tag relations are removed and replaced with the provided list of tag names.
     *
     * @param gameId - id of game to be updated
     * @param request - request body containing optional title, description, banner url and tags
     * @param authentication - authenticated user token
     * @return HTTP status {@code 200} with updated game data in response body
     * @throws ResponseStatusException with code {@code 401} if user token is invalid
     * @Author: Egor Grishin
     */
    @PatchMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Games> updateGame(
            @PathVariable("id") Long gameId,
            @RequestBody UpdateGameRequest request,
            Authentication authentication) {

        gamesControllerLogger.info("Called GamesController /games/id (post)");
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        }

        Long currentUserId = (Long) authentication.getPrincipal();

        Games updatedGame = gamesService.updateGame(request, currentUserId, gameId);
        return ResponseEntity.status(HttpStatus.OK).body(updatedGame);
    }

    /**
     * Handles request for deleting a game by id.
     *
     * @param id - id of game to be deleted
     * @param authentication - authenticated user token
     * @return HTTP status {@code 204} without response body
     * @throws ResponseStatusException with code {@code 401} if user token is invalid
     * @Author: Artemii Gorelov
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Games> deleteGame(
            @PathVariable("id") Long id,
            Authentication authentication
    ) {

        gamesControllerLogger.info("Called GamesController /games/id (delete)");
        if (authentication == null || !authentication.isAuthenticated()) {
            gamesControllerLogger.error("User delete game permissions error");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        }

        Long currentUserId = (Long) authentication.getPrincipal();

        Games deletedGame = gamesService.deleteGame(id, currentUserId);
        return ResponseEntity.status(204).build();
    }

    /**
     * Handles request for returning information about a specific game author.
     *
     * @param id - requested author id
     * @return HTTP status {@code 200} with author information in response body,
     * or {@code 404} if author with requested id is not found
     * @Author: Artemii Gorelov
     */
    @GetMapping("/author/{id}")
    public ResponseEntity<AuthorResponse> getGameAuthor(@PathVariable Long id) {
        gamesControllerLogger.info("Called /games get game author method with id = {}", id);
        AuthorResponse author = gamesService.getAuthorById(id);
        return ResponseEntity.ok(author);
    }

    /**
     * Handles request for returning all available tags grouped by tag type.
     *
     * @return response entity with status {@code 200} and grouped tags in response body
     * @Author: Egor Grishin
     */
    @GetMapping("/tags/all")
    public ResponseEntity<TagsResponse> getAllTags() {
        gamesControllerLogger.info("Called /games/tags/all get all tags method");
        return ResponseEntity.status(HttpStatus.OK).body(gamesService.getAllTags());
    }
}
