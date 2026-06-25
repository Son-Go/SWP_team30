package gde.gde_website.users.model;

/**
 * This is a record for me response
 * @param id - user id
 * @param username - user nickname
 * @param email - user email
 * @param profileImageUrl - user profile image path
 * @Author: Artemii Gorelov
 */
public record MeResponse(
        Long id,
        String username,
        String email,
        String profileImageUrl
) {}