package gde.gde_website;

import gde.gde_website.games.model.AuthorResponse;
import gde.gde_website.games.model.Games;
import gde.gde_website.games.model.GamesCardResponce;
import gde.gde_website.games.model.GamesPageResponce;
import gde.gde_website.games.model.TagsResponse;
import gde.gde_website.games.service.GamesService;
import gde.gde_website.security.JwtFilter;
import gde.gde_website.security.JwtUtils;
import gde.gde_website.security.config.SecurityConfig;
import gde.gde_website.users.model.LoginResponse;
import gde.gde_website.users.model.MeResponse;
import gde.gde_website.users.service.UsersService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@Import({SecurityConfig.class, JwtFilter.class, JwtUtils.class})
@TestPropertySource(properties = "jwt.secret=01234567890123456789012345678901")
class EndpointHttpIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtils jwtUtils;

    @MockitoBean
    private UsersService usersService;

    @MockitoBean
    private GamesService gamesService;

    @Test
    void registerCreatesUserSessionOverHttp() throws Exception {
        when(usersService.register(any())).thenReturn(new LoginResponse("new-user-token"));

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "new-user",
                                  "email": "new@example.com",
                                  "password": "secret",
                                  "profileImageUrl": null
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value("new-user-token"));
    }

    @Test
    void loginReturnsTokenOverHttp() throws Exception {
        when(usersService.login(any())).thenReturn(new LoginResponse("jwt-token"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "user@example.com",
                                  "password": "secret"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"));
    }

    @Test
    void meRequiresJwtOverHttp() throws Exception {
        mockMvc.perform(get("/auth/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Unauthorized"));
    }

    @Test
    void meReturnsCurrentUserWhenJwtIsValid() throws Exception {
        when(usersService.me(42L))
                .thenReturn(new MeResponse(42L, "andrey", "andrey@example.com", null, false));

        mockMvc.perform(get("/auth/me")
                        .header("Authorization", bearerToken(42L)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(42))
                .andExpect(jsonPath("$.username").value("andrey"))
                .andExpect(jsonPath("$.email").value("andrey@example.com"));

        verify(usersService).me(42L);
    }

    @Test
    void gamesListIsPublicAndReturnsPagedJson() throws Exception {
        when(gamesService.getAllGames(PageRequest.of(0, 24)))
                .thenReturn(new PageImpl<>(List.of(
                        new GamesPageResponce(
                                1L,
                                11L,
                                "Portal",
                                "Puzzle platformer",
                                "https://example.com/portal.png",
                                new AuthorResponse("valve", null, "valve@example.com"),
                                List.of("puzzle", "coop")
                        )
                )));

        mockMvc.perform(get("/games")
                        .param("page", "0")
                        .param("size", "24"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].title").value("Portal"))
                .andExpect(jsonPath("$.content[0].tags[0]").value("puzzle"));
    }

    @Test
    void gameDetailsArePublicAndPreserveAnonymousUserContext() throws Exception {
        when(gamesService.getGameById(7L, null)).thenReturn(gameCard(false));

        mockMvc.perform(get("/games/7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(7))
                .andExpect(jsonPath("$.title").value("Hades"))
                .andExpect(jsonPath("$.isOwner").value(false));

        verify(gamesService).getGameById(7L, null);
    }

    @Test
    void createGameRequiresJwtOverHttp() throws Exception {
        mockMvc.perform(post("/games")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createGameJson()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void updateGameRequiresJwtOverHttp() throws Exception {
        mockMvc.perform(patch("/games/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Updated Game",
                                  "description": "Updated description",
                                  "bannerUrl": "https://example.com/banner.png",
                                  "gameTags": ["indie"],
                                  "screenshots": []
                                }
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deleteGameRequiresJwtOverHttp() throws Exception {
        mockMvc.perform(delete("/games/5"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createGameUsesAuthenticatedUserFromJwt() throws Exception {
        when(gamesService.createGame(any(), eq(42L))).thenReturn(game());

        mockMvc.perform(post("/games")
                        .header("Authorization", bearerToken(42L))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createGameJson()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.authorId").value(42))
                .andExpect(jsonPath("$.title").value("New Game"));

        verify(gamesService).createGame(any(), eq(42L));
    }

    @Test
    void updateGameUsesAuthenticatedUserFromJwt() throws Exception {
        when(gamesService.updateGame(any(), eq(42L), eq(5L))).thenReturn(game());

        mockMvc.perform(patch("/games/5")
                        .header("Authorization", bearerToken(42L))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Updated Game",
                                  "description": "Updated description",
                                  "bannerUrl": "https://example.com/banner.png",
                                  "gameTags": ["indie"],
                                  "screenshots": []
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.title").value("New Game"));

        verify(gamesService).updateGame(any(), eq(42L), eq(5L));
    }

    @Test
    void deleteGameUsesAuthenticatedUserFromJwt() throws Exception {
        when(gamesService.deleteGame(5L, 42L)).thenReturn(game());

        mockMvc.perform(delete("/games/5")
                        .header("Authorization", bearerToken(42L)))
                .andExpect(status().isNoContent());

        verify(gamesService).deleteGame(5L, 42L);
    }

    @Test
    void tagsEndpointReturnsAvailableTagsOverHttp() throws Exception {
        when(gamesService.getAllTags()).thenReturn(new TagsResponse(List.of("puzzle", "indie")));

        mockMvc.perform(get("/games/tags/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gameTags[0]").value("puzzle"))
                .andExpect(jsonPath("$.gameTags[1]").value("indie"));
    }

    @Test
    void authorEndpointReturnsPublicAuthorContractOverHttp() throws Exception {
        when(gamesService.getAuthorById(15L))
                .thenReturn(new AuthorResponse("supergiant", null, "studio@example.com"));

        mockMvc.perform(get("/games/author/15"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("supergiant"))
                .andExpect(jsonPath("$.profile_image_url").doesNotExist())
                .andExpect(jsonPath("$.email").value("studio@example.com"));
    }

    private String bearerToken(Long userId) {
        return "Bearer " + jwtUtils.generateToken(userId);
    }

    private String createGameJson() {
        return """
                {
                  "title": "New Game",
                  "description": "Description",
                  "bannerUrl": "https://example.com/banner.png",
                  "gameTags": ["indie"],
                  "screenshots": ["https://example.com/screenshot.png"]
                }
                """;
    }

    private Games game() {
        return new Games(
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
    }

    private GamesCardResponce gameCard(boolean isOwner) {
        return new GamesCardResponce(
                7L,
                15L,
                "Hades",
                "Roguelike action",
                "https://example.com/hades.png",
                Instant.parse("2026-01-01T00:00:00Z"),
                Instant.parse("2026-01-02T00:00:00Z"),
                isOwner,
                new AuthorResponse("supergiant", null, "studio@example.com"),
                List.of("action"),
                List.of("https://example.com/screenshot.png")
        );
    }
}
