package gde.gde_website.users.controller;

import gde.gde_website.users.model.LoginRequest;
import gde.gde_website.users.model.LoginResponse;
import gde.gde_website.users.model.MeResponse;
import gde.gde_website.users.service.UsersService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsersControllerTest {

    @Mock
    private UsersService usersService;

    @InjectMocks
    private UsersController usersController;

    @Test
    void loginReturnsToken() {
        LoginRequest request = new LoginRequest("user@example.com", "secret");
        LoginResponse expected = new LoginResponse("jwt-token");

        when(usersService.login(request)).thenReturn(expected);

        ResponseEntity<LoginResponse> response = usersController.login(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expected, response.getBody());
    }

    @Test
    void meReturnsUnauthorizedWithoutAuthentication() {
        ResponseEntity<MeResponse> response = usersController.me(null);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void meReturnsCurrentUserWhenAuthenticated() {
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(42L, null, List.of());
        MeResponse expected = new MeResponse(42L, "andrey", "andrey@example.com", null, false);

        when(usersService.me(42L)).thenReturn(expected);

        ResponseEntity<MeResponse> response = usersController.me(authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expected, response.getBody());
        verify(usersService).me(42L);
    }
}
