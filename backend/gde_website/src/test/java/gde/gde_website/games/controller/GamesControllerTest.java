package gde.gde_website.games.controller;

import gde.gde_website.games.model.*;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
        Map<String, List<String>> tags = new LinkedHashMap<>();
        tags.put("GENRE", List.of("puzzle", "coop"));
        tags.put("MODE", List.of());

        Page<GamesPageResponse> expectedPage = new PageImpl<>(List.of(
                new GamesPageResponse(
                        1L,
                        11L,
                        "Portal",
                        "Puzzle platformer",
                        "https://example.com/portal.png",
                        new AuthorResponse("valve", null, "valve@example.com"),
                        tags
                )
        ));

        when(gamesService.getAllGames(org.springframework.data.domain.PageRequest.of(0, 24)))
                .thenReturn(expectedPage);

        ResponseEntity<Page<GamesPageResponse>> response = gamesController.getAllGames(0, 24, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedPage, response.getBody());
    }

    @Test
    void getGameByIdPassesAuthenticatedUserToService() {
        GamesCardResponse expected = new GamesCardResponse(
                7L,
                15L,
                "Hades",
                "Roguelike action",
                "https://example.com/hades.png",
                Instant.parse("2026-01-01T00:00:00Z"),
                Instant.parse("2026-01-02T00:00:00Z"),
                true,
                new AuthorResponse("supergiant", null, "studio@example.com"),
                List.of("action"),
                List.of("https://example.com/screenshot.png")
        );
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(99L, null, List.of());

        when(gamesService.getGameById(7L, 99L)).thenReturn(expected);

        ResponseEntity<GamesCardResponse> response = gamesController.getGameById(7L, authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expected, response.getBody());
        verify(gamesService).getGameById(7L, 99L);
    }

    @Test
    void createGameRequiresAuthentication() {
        GamesCreateRequest request = new GamesCreateRequest(
                "New Game",
                "Description",
                "https://example.com/banner.png",
                List.of("indie"),
                List.of("https://example.com/screenshot.png")
        );

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> gamesController.createGame(request, null)
        );

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
    }

    @Test
    void createGameReturnsCreatedGameForAuthenticatedUser() {
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(42L, null, List.of());
        GamesCreateRequest request = new GamesCreateRequest(
                "New Game",
                "Description",
                "https://example.com/banner.png",
                List.of("indie"),
                List.of("https://example.com/screenshot.png")
        );
        Games expected = new Games(
                5L,
                42L,
                "New Game",
                "Description",
                "https://example.com/banner.png",
                Instant.parse("2026-01-01T00:00:00Z"),
                Instant.parse("2026-01-02T00:00:00Z"),
                List.of("indie"),
                List.of("https://example.com/screenshot.png")
        );

        when(gamesService.createGame(request, 42L))
                .thenReturn(expected);

        ResponseEntity<Games> response = gamesController.createGame(request, authentication);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(expected, response.getBody());
        verify(gamesService).createGame(request, 42L);
    }
}
