package gde.gde_website.users.controller;

import gde.gde_website.games.model.GamesPageResponse;
import gde.gde_website.users.model.ChangePasswordRequest;
import gde.gde_website.users.model.PublicUserProfileResponse;
import gde.gde_website.users.model.UpdateProfileRequest;
import gde.gde_website.users.model.UserProfileUpdateResponse;
import gde.gde_website.users.service.UsersProfileService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Controller that handles HTTP requests related to user profile management,
 * such as updating profile fields, changing password, deleting account,
 * and viewing public profiles and games of other users.
 *
 * @Author: Artemii Gorelov
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UsersProfileController {
    private static final Logger usersProfileControllerLogger = LoggerFactory.getLogger(UsersProfileController.class);
    private final UsersProfileService usersProfileService;

    /**
     * Updates the profile of the currently authenticated user.
     *
     * @param userId - id of the currently authenticated user, extracted from the JWT token
     * @param request - profile fields to update (username, email, profileImageUrl)
     * @return HTTP status OK (code {@code 200}) with updated {@link UserProfileUpdateResponse} in body
     * @throws org.springframework.web.server.ResponseStatusException with code {@code 404} if user is not found
     * @throws org.springframework.web.server.ResponseStatusException with code {@code 409} if requested username or email is already taken
     * @Author: Artemii Gorelov
     */
    @PatchMapping("/me")
    public ResponseEntity<UserProfileUpdateResponse> updateProfile(
            @AuthenticationPrincipal Long userId,
            @RequestBody UpdateProfileRequest request
    ) {
        return ResponseEntity.ok(usersProfileService.updateProfile(userId, request));
    }

    /**
     * Changes the password of the currently authenticated user.
     *
     * @param userId - id of the currently authenticated user, extracted from the JWT token
     * @param request - old and new password values
     * @return HTTP status NO_CONTENT (code {@code 204}) if password was changed successfully
     * @throws org.springframework.web.server.ResponseStatusException with code {@code 404} if user is not found
     * @throws org.springframework.web.server.ResponseStatusException with code {@code 400} if old password is incorrect or new password is blank
     * @Author: Artemii Gorelov
     */
    @PatchMapping("/me/password")
    public ResponseEntity<Void> changePassword(
            @AuthenticationPrincipal Long userId,
            @RequestBody ChangePasswordRequest request
    ) {
        usersProfileService.changePassword(userId, request);
        return ResponseEntity.noContent().build();
    }

    /**
     * Deletes the account of the currently authenticated user, along with all games created by them.
     *
     * @param userId - id of the currently authenticated user, extracted from the JWT token
     * @return HTTP status NO_CONTENT (code {@code 204}) if account was deleted successfully
     * @throws org.springframework.web.server.ResponseStatusException with code {@code 404} if user is not found
     * @Author: Artemii Gorelov
     */
    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteAccount(@AuthenticationPrincipal Long userId) {
        usersProfileService.deleteAccount(userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Returns the public profile of a user by id.
     * This endpoint is accessible without authentication.
     *
     * @param id - id of the user whose public profile is requested
     * @return HTTP status OK (code {@code 200}) with {@link PublicUserProfileResponse} in body
     * @throws org.springframework.web.server.ResponseStatusException with code {@code 404} if user is not found
     * @Author: Artemii Gorelov
     */
    @GetMapping("/{id}")
    public ResponseEntity<PublicUserProfileResponse> getPublicProfile(@PathVariable Long id) {
        return ResponseEntity.ok(usersProfileService.getPublicProfile(id));
    }

    /**
     * Returns a paginated list of games created by a specific user.
     * This endpoint is accessible without authentication.
     *
     * @param id - id of the author whose games are requested
     * @param page - initial page, defaults to 0
     * @param size - number of elements on each page, defaults to 24
     * @return HTTP status OK (code {@code 200}) with page of {@link GamesPageResponse} in body
     * @throws org.springframework.web.server.ResponseStatusException with code {@code 404} if user is not found
     * @Author: Artemii Gorelov
     */
    @GetMapping("/{id}/games")
    public ResponseEntity<Page<GamesPageResponse>> getUserGames(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "24") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(usersProfileService.getUserGames(id, pageable));
    }
}