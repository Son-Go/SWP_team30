package gde.gde_website.users.model;

/**
 * Response returned after successfully updating current user's profile.
 * @param username - username after update
 * @param email - email after update
 * @param profileImageUrl - profile image URL after update
 * @Author: Artemii Gorelov
 */
public record UpdateProfileRequest(
        String username,
        String email,
        String profileImageUrl
) {
}