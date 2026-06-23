package gde.gde_website.games.controller;

import gde.gde_website.games.model.Games;
import gde.gde_website.games.model.GamesCardResponce;
import gde.gde_website.games.model.GamesPageResponce;
import gde.gde_website.games.service.GamesService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is used for handling specific HTTP requests which includes /games/ in their paths
 * @Author: Artemii Gorelov, Egor Grishin
 */
@RestController
@RequestMapping("/games")
@RequiredArgsConstructor
public class GamesController {
    private static final Logger gamesControllerLogger = LoggerFactory.getLogger(GamesController.class);

    private final GamesService gamesService;

    /**
     * This method is used for handling get list of all games divided on the groups of specific size request
     * @param page - initial page
     * @param size - number of elements on each page
     * @return - returns status OK  (code 200) with sublist of games entity inside response body
     * @Author: Artemii Gorelov
     */
    @GetMapping
    public ResponseEntity<Page<GamesPageResponce>> getAllGames(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "24") int size
    ) {

        gamesControllerLogger.info("Called GamesController /games method (get)");
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt"));
        return ResponseEntity.status(HttpStatus.OK).body(gamesService.getAllGames(pageable));
    }

    /**
     * This method is used for handling get game by specific id request
     * @param id - game id
     * @param authentication - user token
     * @return - status OK (code 200) with requested game inside response body
     * @Author: Egor Grishin
     */
    @GetMapping("/{id}")
    public ResponseEntity<GamesCardResponce> getGameById(
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
     * This method is used for handling create new game request
     * @param title - title of game to be created
     * @param description - description of game to be created
     * @param bannerUrl - banner url path of game to be created
     * @param authentication - user token
     * @return http status CREATED (code 201) with created game info inside response body
     * @throws ResponseStatusException with code 401 while user is not authenticated
     * @Author: Artemii Gorelov
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Games> createGame(
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam String bannerUrl,
            @RequestParam(required = false) List<String> gameTags,
            Authentication authentication
    ) {

        gamesControllerLogger.info("Called GamesController /games (post)");
        if (authentication == null || !authentication.isAuthenticated()) {
            gamesControllerLogger.error("User create game permissions error");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        }

        Long currentUserId = (Long) authentication.getPrincipal();

        Games gameWithCurrentAuthor = createNewRawGame(currentUserId, title, description, bannerUrl, gameTags);

        Games createGame = gamesService.createGame(gameWithCurrentAuthor, currentUserId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createGame);
    }

    /**
     * This method is used for handling update request for game with specific id
     * @param gameId - id of game to pe updated
     * @param title - title to be updated
     * @param description - description to be updated
     * @param bannerUrl - banner url path to be updated
     * @param authentication - user token
     * @return Http status ok (code 200) with body of updated game
     * @throws ResponseStatusException with code 401 while user token is invalid
     * @Author: Egor Grishin
     */
    @PatchMapping(path = "/{id}", consumes = {
            MediaType.MULTIPART_FORM_DATA_VALUE,
            MediaType.APPLICATION_FORM_URLENCODED_VALUE
    })
    public ResponseEntity<Games> updateGame(
            @PathVariable("id") Long gameId,
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam String bannerUrl,
            @RequestParam(required = false) List<String> gameTags,
            Authentication authentication) {

        gamesControllerLogger.info("Called GamesController /games/id (post)");
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        }

        Long currentUserId = (Long) authentication.getPrincipal();

        Games gameToUpdate = createNewRawGame(currentUserId, title, description, bannerUrl, gameTags);

        Games updatedGame = gamesService.updateGame(gameToUpdate, gameId);
        return ResponseEntity.status(HttpStatus.OK).body(updatedGame);
    }

    /**
     * This function is used for handling delete game request
     * @param id - id of game to be deleted
     * @param authentication - user token
     * @return Http status 204 without body
     * @throws ResponseStatusException with code 401 while user token is invalid
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
     * This function is used for creating new raw game (without id and creation time for database)
     * @param currentUserId - id of user which creating game
     * @param title - title of the game
     * @param description - game description
     * @param bannerUrl - game banner url path
     * @return new Games object
     * @Author: Egor Grishin
     */
    private Games createNewRawGame(Long currentUserId,
                                   String title,
                                   String description,
                                   String bannerUrl,
                                   List<String> gameTags) {
        return new Games(
                null,
                currentUserId,
                title,
                description,
                bannerUrl,
                null,
                null,
                gameTags
        );
    }
}
