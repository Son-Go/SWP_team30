package gde.gde_website.users.model;

/**
 * This is a record for login request
 * @param email - user email
 * @param password - user password
 * @Author: Egor Grishin
 */
public record LoginRequest(
        String email,
        String password
) {
}
