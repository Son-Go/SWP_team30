package gde.gde_website.users.model;

/**
 * This is a record for login request
 * @param authInfo - user email or username
 * @param isEmail - true if authInfo contains email, false if authInfo contains username
 * @param password - user password
 * @Author: Egor Grishin
 */
public record LoginRequest(
        String authInfo,
        boolean isEmail,
        String password
) {
}
