package gde.gde_website.users.controller;

import gde.gde_website.users.model.BanRequest;
import gde.gde_website.users.model.PromoteRequest;
import gde.gde_website.users.model.UserListResponse;
import gde.gde_website.users.service.UsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * This class is used for handling admin with users interactions requests
 * @Author: Artemii Gorelov
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("admin/users")
public class AdminUsersController {
    private final UsersService usersService;

    /**
     * This method is used for handling {@code GET} users request
     * @param authentication - authentication principal
     * @return - returns HTTP status with code {@code 200} and list of all users
     * @Author: Artemii Gorelov
     */
    @GetMapping
    public ResponseEntity<UserListResponse> getAllUsers(Authentication authentication) {
        return ResponseEntity.ok(usersService.getAllUsers((Long) authentication.getPrincipal()));
    }

    /**
     * This method is used for handling promoting request
     * @param promoteRequest - body of promote request which contains userId
     * @param authentication - authentication principal
     * @return - HTTP status with code {@code 200} when user is promoted
     * @Author: Artemii Gorelov
     */
    @PostMapping("promote")
    public ResponseEntity<Void> promote(
            @RequestBody PromoteRequest promoteRequest,
            Authentication authentication
    ) {
        usersService.promoteUserToAdmin(promoteRequest.userId(), (Long) authentication.getPrincipal());
        return ResponseEntity.ok().build();
    }

    /**
     * This method is used for handling demoting request
     * @param request - contains userId
     * @param authentication - authentication principal
     * @return - HTTP status with code {@code 200} while user is demoted
     * @Author: Artemii Gorelov
     */
    @PostMapping("demote")
    public ResponseEntity<Void> demote(
            @RequestBody PromoteRequest request,
            Authentication authentication
    ) {
        usersService.demoteUserFromAdmin(request.userId(), (Long) authentication.getPrincipal());
        return ResponseEntity.ok().build();
    }

    /**
     * This method is used for handling ban request
     * @param request - contains user id
     * @param authentication - authentication principal
     * @return - HTTP status with code {@code 200} while user is banned
     * @Author: Artemii Gorelov
     */
    @PostMapping("ban")
    public ResponseEntity<Void> ban(
            @RequestBody BanRequest request,
            Authentication authentication
    ) {
        usersService.banUser(request.userId(), (Long) authentication.getPrincipal());
        return ResponseEntity.ok().build();
    }

    /**
     * This method is used for handling unban request
     * @param request - contains user id
     * @param authentication - authentication principal
     * @return - HTTP status with code {@code 200} when user is unbanned
     * @Author: Artemii Gorelov
     */
    @PostMapping("unban")
    public ResponseEntity<Void> unban(
            @RequestBody BanRequest request,
            Authentication authentication
    ) {
        usersService.unbanUser(request.userId(), (Long) authentication.getPrincipal());
        return  ResponseEntity.ok().build();
    }

    /**
     * This method is used for handling user deleting request
     * @param id - id of user to be deleted
     * @param authentication - authentication principal
     * @return - HTTP status with code {@code 200} when user is deleted
     * @Author: Artemii Gorelov
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            Authentication authentication
    )  {
        usersService.deleteUser(id, (Long) authentication.getPrincipal());
        return ResponseEntity.ok().build();
    }
}
