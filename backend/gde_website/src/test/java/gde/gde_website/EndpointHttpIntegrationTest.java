package gde.gde_website;

import gde.gde_website.games.model.*;
import gde.gde_website.games.service.GamesCommentsService;
import gde.gde_website.games.service.GamesService;
import gde.gde_website.security.JwtFilter;
import gde.gde_website.security.JwtUtils;
import gde.gde_website.security.config.SecurityConfig;
import gde.gde_website.users.model.LoginResponse;
import gde.gde_website.users.model.MeResponse;
import gde.gde_website.users.model.PublicUserProfileResponse;
import gde.gde_website.users.model.UserProfileUpdateResponse;
import gde.gde_website.users.model.UserRole;
import gde.gde_website.users.service.UsersProfileService;
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
    private UsersProfileService usersProfileService;

    @MockitoBean
    private GamesService gamesService;

    @MockitoBean
    private GamesCommentsService gamesCommentsService;

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
                                  "authInfo": "user@example.com",
                                  "isEmail": true,
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
                .thenReturn(new MeResponse(42L, "andrey", "andrey@example.com", null, false, UserRole.DEVELOPER));

        mockMvc.perform(get("/auth/me")
                        .header("Authorization", bearerToken(42L, UserRole.DEVELOPER)))
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
                        new GamesPageResponse(
                                1L,
                                11L,
                                "Portal",
                                "Puzzle platformer",
                                "Puzzle platformer",
                                "https://example.com/portal.png",
                                new AuthorResponse("valve", null, "valve@example.com"),
                                true,
                                pageTags(),
                                List.of("https://example.com/portal-shot.png")
                        )
                )));

        mockMvc.perform(get("/games")
                        .param("page", "0")
                        .param("size", "24"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].title").value("Portal"))
                .andExpect(jsonPath("$.content[0].gameTags.GENRE[0]").value("puzzle"))
                .andExpect(jsonPath("$.content[0].gameTags.GENRE[1]").value("coop"))
                .andExpect(jsonPath("$.content[0].gameTags.MODE").isArray());
    }

    @Test
    void gameDetailsArePublicAndPreserveAnonymousUserContext() throws Exception {
        when(gamesService.getGameById(7L, null)).thenReturn(gameCard(false));

        mockMvc.perform(get("/games/7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(7))
                .andExpect(jsonPath("$.title").value("Hades"))
                .andExpect(jsonPath("$.isOwner").value(false))
                .andExpect(jsonPath("$.screenshots.videos[0]").value("https://example.com/trailer.mp4"))
                .andExpect(jsonPath("$.screenshots.pictures[0]").value("https://example.com/screenshot.png"));

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
                                  "screenshots": {
                                    "videos": [],
                                    "pictures": []
                                  }
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
                        .header("Authorization", bearerToken(42L, UserRole.DEVELOPER))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createGameJson()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.authorId").value(42))
                .andExpect(jsonPath("$.title").value("New Game"))
                .andExpect(jsonPath("$.screenshots.videos[0]").value("https://example.com/trailer.mp4"))
                .andExpect(jsonPath("$.screenshots.pictures[0]").value("https://example.com/screenshot.png"));

        verify(gamesService).createGame(any(), eq(42L));
    }

    @Test
    void updateGameUsesAuthenticatedUserFromJwt() throws Exception {
        when(gamesService.updateGame(any(), eq(42L), eq(5L))).thenReturn(game());

        mockMvc.perform(patch("/games/5")
                        .header("Authorization", bearerToken(42L, UserRole.DEVELOPER))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Updated Game",
                                  "description": "Updated description",
                                  "bannerUrl": "https://example.com/banner.png",
                                  "gameTags": ["indie"],
                                  "screenshots": {
                                    "videos": [],
                                    "pictures": []
                                  }
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.title").value("New Game"))
                .andExpect(jsonPath("$.screenshots.videos[0]").value("https://example.com/trailer.mp4"))
                .andExpect(jsonPath("$.screenshots.pictures[0]").value("https://example.com/screenshot.png"));

        verify(gamesService).updateGame(any(), eq(42L), eq(5L));
    }

    @Test
    void deleteGameUsesAuthenticatedUserFromJwt() throws Exception {
        when(gamesService.deleteGame(5L, 42L)).thenReturn(game());

        mockMvc.perform(delete("/games/5")
                        .header("Authorization", bearerToken(42L, UserRole.DEVELOPER)))
                .andExpect(status().isNoContent());

        verify(gamesService).deleteGame(5L, 42L);
    }

    @Test
    void tagsEndpointReturnsAvailableTagsOverHttp() throws Exception {
        when(gamesService.getAllTags()).thenReturn(new TagsResponse(groupedTags("puzzle", "indie")));

        mockMvc.perform(get("/games/tags/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gameTags.GENRE[0]").value("puzzle"))
                .andExpect(jsonPath("$.gameTags.GENRE[1]").value("indie"))
                .andExpect(jsonPath("$.gameTags.MODE").isArray());
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

    @Test
    void updateProfileRequiresJwtOverHttp() throws Exception {
        mockMvc.perform(patch("/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "andrey-updated",
                                  "email": "updated@example.com",
                                  "profileImageUrl": "https://example.com/avatar.png"
                                }
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void updateProfileUsesAuthenticatedUserFromJwt() throws Exception {
        when(usersProfileService.updateProfile(any(), any()))
                .thenReturn(new UserProfileUpdateResponse(
                        42L,
                        "andrey-updated",
                        "updated@example.com",
                        "https://example.com/avatar.png"
                ));

        mockMvc.perform(patch("/users/me")
                        .header("Authorization", bearerToken(42L, UserRole.DEVELOPER))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "andrey-updated",
                                  "email": "updated@example.com",
                                  "profileImageUrl": "https://example.com/avatar.png"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(42))
                .andExpect(jsonPath("$.username").value("andrey-updated"))
                .andExpect(jsonPath("$.email").value("updated@example.com"))
                .andExpect(jsonPath("$.profileImageUrl").value("https://example.com/avatar.png"));

        verify(usersProfileService).updateProfile(eq(42L), any());
    }

    @Test
    void changePasswordRequiresJwtOverHttp() throws Exception {
        mockMvc.perform(patch("/users/me/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "oldPassword": "old-secret",
                                  "newPassword": "new-secret"
                                }
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void changePasswordUsesAuthenticatedUserFromJwt() throws Exception {
        mockMvc.perform(patch("/users/me/password")
                        .header("Authorization", bearerToken(42L, UserRole.DEVELOPER))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "oldPassword": "old-secret",
                                  "newPassword": "new-secret"
                                }
                                """))
                .andExpect(status().isNoContent());

        verify(usersProfileService).changePassword(eq(42L), any());
    }

    @Test
    void deleteAccountRequiresJwtOverHttp() throws Exception {
        mockMvc.perform(delete("/users/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deleteAccountUsesAuthenticatedUserFromJwt() throws Exception {
        mockMvc.perform(delete("/users/me")
                        .header("Authorization", bearerToken(42L, UserRole.DEVELOPER)))
                .andExpect(status().isNoContent());

        verify(usersProfileService).deleteAccount(42L);
    }

    @Test
    void publicProfileIsPublicAndReturnsUserContractOverHttp() throws Exception {
        when(usersProfileService.getPublicProfile(15L))
                .thenReturn(new PublicUserProfileResponse(
                        15L,
                        "supergiant",
                        "https://example.com/studio.png",
                        3L
                ));

        mockMvc.perform(get("/users/15"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(15))
                .andExpect(jsonPath("$.username").value("supergiant"))
                .andExpect(jsonPath("$.profileImageUrl").value("https://example.com/studio.png"))
                .andExpect(jsonPath("$.gameCount").value(3));
    }

    @Test
    void publicUserGamesArePublicAndReturnPagedJson() throws Exception {
        when(usersProfileService.getUserGames(eq(15L), eq(PageRequest.of(0, 24))))
                .thenReturn(new PageImpl<>(List.of(
                        new GamesPageResponse(
                                7L,
                                15L,
                                "Hades",
                                "Roguelike action",
                                "Roguelike action",
                                "https://example.com/hades.png",
                                new AuthorResponse("supergiant", null, "studio@example.com"),
                                true,
                                pageTags(),
                                List.of("https://example.com/hades-shot.png")
                        )
                )));

        mockMvc.perform(get("/users/15/games")
                        .param("page", "0")
                        .param("size", "24"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(7))
                .andExpect(jsonPath("$.content[0].title").value("Hades"))
                .andExpect(jsonPath("$.content[0].author.username").value("supergiant"))
                .andExpect(jsonPath("$.content[0].gameTags.GENRE[0]").value("puzzle"));
    }

    private String bearerToken(Long userId, UserRole userRole) {
        return "Bearer " + jwtUtils.generateToken(userId, userRole);
    }

    private String createGameJson() {
        return """
                {
                  "title": "New Game",
                  "shortDescription": "Short description",
                  "description": "Description",
                  "bannerUrl": "https://example.com/banner.png",
                  "gameTags": ["indie"],
                  "screenshots": {
                    "videos": ["https://example.com/trailer.mp4"],
                    "pictures": ["https://example.com/screenshot.png"]
                  }
                }
                """;
    }

    private Games game() {
        return new Games(
                5L,
                42L,
                "New Game",
                "Short description",
                "Description",
                "https://example.com/banner.png",
                Instant.parse("2026-01-01T00:00:00Z"),
                Instant.parse("2026-01-02T00:00:00Z"),
                groupedTags("indie"),
                groupedScreenshots()
        );
    }

    private java.util.Map<String, java.util.List<String>> pageTags() {
        java.util.Map<String, java.util.List<String>> tags = new java.util.LinkedHashMap<>();
        tags.put("GENRE", java.util.List.of("puzzle", "coop"));
        tags.put("MODE", java.util.List.of());
        return tags;
    }

    private java.util.Map<String, java.util.List<String>> groupedTags(String... values) {
        java.util.Map<String, java.util.List<String>> tags = new java.util.LinkedHashMap<>();
        tags.put("GENRE", java.util.List.of(values));
        tags.put("MODE", java.util.List.of());
        return tags;
    }

    private java.util.Map<String, java.util.List<String>> groupedScreenshots() {
        java.util.Map<String, java.util.List<String>> screenshots = new java.util.LinkedHashMap<>();
        screenshots.put("videos", java.util.List.of("https://example.com/trailer.mp4"));
        screenshots.put("pictures", java.util.List.of("https://example.com/screenshot.png"));
        return screenshots;
    }

    private GamesCardResponse gameCard(boolean isOwner) {
        return new GamesCardResponse(
                7L,
                15L,
                "Hades",
                "Roguelike action",
                "Roguelike action",
                "https://example.com/hades.png",
                Instant.parse("2026-01-01T00:00:00Z"),
                Instant.parse("2026-01-02T00:00:00Z"),
                isOwner,
                true,
                new AuthorResponse("supergiant", null, "studio@example.com"),
                groupedTags("action"),
                groupedScreenshots()
        );
    }
}
