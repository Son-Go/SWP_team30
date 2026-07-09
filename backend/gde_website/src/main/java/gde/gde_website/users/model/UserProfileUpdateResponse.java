package gde.gde_website.users.model;

/**
 * Response returned after successfully updating current user's profile.
 * @param id - user id
 * @param username - current username after updating
 * @param email - current user email after updating
 * @param profileImageUrl - current user profile image URL after updating
 * @Author: Artemii Gorelov
 */
public record UserProfileUpdateResponse(
        Long id,
        String username,
        String email,
        String profileImageUrl
) {}