package gde.gde_website.games.controller;

import gde.gde_website.games.model.AuthorResponse;
import gde.gde_website.games.model.Games;
import gde.gde_website.games.model.GamesCardResponce;
import gde.gde_website.games.model.GamesPageResponce;
import gde.gde_website.games.service.GamesService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GamesControllerTest {

    @Mock
    private GamesService gamesService;

    @InjectMocks
    private GamesController gamesController;

    @Test
    void getAllGamesReturnsPagedGames() {
        Page<GamesPageResponce> expectedPage = new PageImpl<>(List.of(
                new GamesPageResponce(
                        1L,
                        11L,
                        "Portal",
                        "Puzzle platformer",
                        "https://example.com/portal.png",
                        new AuthorResponse("valve", null, "valve@example.com"),
                        List.of("puzzle", "coop")
                )
        ));

        when(gamesService.getAllGames(org.springframework.data.domain.PageRequest.of(0, 24)))
                .thenReturn(expectedPage);

        ResponseEntity<Page<GamesPageResponce>> response = gamesController.getAllGames(0, 24, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedPage, response.getBody());
    }

    @Test
    void getGameByIdPassesAuthenticatedUserToService() {
        GamesCardResponce expected = new GamesCardResponce(
                7L,
                15L,
                "Hades",
                "Roguelike action",
                "https://example.com/hades.png",
                Instant.parse("2026-01-01T00:00:00Z"),
                Instant.parse("2026-01-02T00:00:00Z"),
                true,
                new AuthorResponse("supergiant", null, "studio@example.com"),
                List.of("action")
        );
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(99L, null, List.of());

        when(gamesService.getGameById(7L, 99L)).thenReturn(expected);

        ResponseEntity<GamesCardResponce> response = gamesController.getGameById(7L, authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expected, response.getBody());
        verify(gamesService).getGameById(7L, 99L);
    }

    @Test
    void createGameRequiresAuthentication() {
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> gamesController.createGame(
                        "New Game",
                        "Description",
                        "https://example.com/banner.png",
                        List.of("indie"),
                        null
                )
        );

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
    }

    @Test
    void createGameReturnsCreatedGameForAuthenticatedUser() {
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(42L, null, List.of());
        Games expected = new Games(
                5L,
                42L,
                "New Game",
                "Description",
                "https://example.com/banner.png",
                Instant.parse("2026-01-01T00:00:00Z"),
                Instant.parse("2026-01-02T00:00:00Z"),
                List.of("indie")
        );

        when(gamesService.createGame(org.mockito.ArgumentMatchers.any(Games.class), org.mockito.ArgumentMatchers.eq(42L)))
                .thenReturn(expected);

        ResponseEntity<Games> response = gamesController.createGame(
                "New Game",
                "Description",
                "https://example.com/banner.png",
                List.of("indie"),
                authentication
        );

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(expected, response.getBody());
        verify(gamesService).createGame(org.mockito.ArgumentMatchers.any(Games.class), org.mockito.ArgumentMatchers.eq(42L));
    }
}
