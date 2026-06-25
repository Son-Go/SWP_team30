package gde.gde_website.users.controller;

import gde.gde_website.users.model.*;
import gde.gde_website.users.service.UsersService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Class that handles HTTP requests with "/auth/" path inside them
 * @Author: Artemii Gorelov, Egor Grishin
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class UsersController {
    private static final Logger usersControllerLogger = LoggerFactory.getLogger(UsersController.class);

    private final UsersService userService;

    /**
     *This function is used for handling registration request
     * @param request - New user registration request
     * It contains following user information:
     *                username - new user username to be assigned,
     *                email - new user email to be saved,
     *                password - new user password,
     *                profileImageUrl - profile image of the new user
     * @return - HTTP status CREATED (code 201) with LoginResponse in body
     * LoginResponse contains following information:
     * token - current authenticated session token
     *
     * @Author: Egor Grishin
     */
    @PostMapping("/register")
    public ResponseEntity<LoginResponse> register(
            @RequestBody RegisterRequest request
    ) {
        usersControllerLogger.info("Called UsersController /auth/register method");
        LoginResponse response = userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * This function is used for handling user login request
     * @param request - Login user request
     * It contains following user information:
     *                email,
     *                password
     * @return - HTTP status OK (code 200) with userService.login(request) in body
     * userService.login(request) function returns following information:
     * token - current authenticated session token
     *
     * @Author: Egor Grishin
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody LoginRequest request
            ) {
        usersControllerLogger.info("Called UsersController /auth/login method");
        return ResponseEntity.status(HttpStatus.OK).body(userService.login(request));
    }

    /**
     * This function is used for handling me request
     * @param authentication - represent current log-inned user token.
     * @return - HTTP status OK (code 200) with MeResponse in body.
     * It contains following information about user:
     * ID - log-inned user ID,
     * username - log-inned user username,
     * email - log-inned user email,
     * profileImageUrl - path to profile image of log-inned user
     *
     * @Author: Artemii Gorelov
     */
    @GetMapping("/me")
    public ResponseEntity<MeResponse> me(
            Authentication authentication
    ) {
        usersControllerLogger.info("Called UsersController /auth/me method");
        if (authentication == null || authentication.getPrincipal() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Long userId = (Long) authentication.getPrincipal();
        MeResponse response = userService.me(userId);

        return ResponseEntity.ok(response);
    }
}
