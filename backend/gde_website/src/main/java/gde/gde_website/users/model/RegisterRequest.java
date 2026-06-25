package gde.gde_website.users.model;

/**
 * This is a record for registration request
 * @param username - user nickname
 * @param email - user email
 * @param password - user password
 * @param profileImageUrl - user profile image path
 * @Author: Egor Grishin
 */
public record RegisterRequest(
        String username,
        String email,
        String password,
        String profileImageUrl
) {}