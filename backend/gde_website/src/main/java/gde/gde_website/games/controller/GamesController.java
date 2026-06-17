package gde.gde_website.games.controller;

import gde.gde_website.games.entity.GamesEntity;
import gde.gde_website.games.model.Games;
import gde.gde_website.games.repository.GamesRepository;
import gde.gde_website.games.service.GamesService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/games")
@RequiredArgsConstructor
public class GamesController {
    private static final Logger gamesControllerLogger = LoggerFactory.getLogger(GamesController.class);

    private final GamesService gamesService;

    private final GamesRepository gamesRepository;

    @GetMapping
    public Page<GamesEntity> getAllGames(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "24") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt"));
        return gamesRepository.findAllByOrderByCreatedAtDesc(pageable);
    }

    // #TODO: implement this function
    // This function must return one game with specific requested id
    @GetMapping("/{id}")
    public ResponseEntity<Games> getGameById(@PathVariable("id") Long id) {
        return null;
    }

    // #TODO: implement this function
    // This function must save new created game into DB
    @PostMapping("/create")
    public ResponseEntity<Games> createGame(@RequestBody Games gameToBeCreated) {
        return null;
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
    public ResponseEntity<Games> deleteGame(@PathVariable("id") Long id) {
        return null;
    }
}
