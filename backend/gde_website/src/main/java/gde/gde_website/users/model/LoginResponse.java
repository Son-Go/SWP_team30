package gde.gde_website.users.model;

/**
 * This is a record for login response
 * @param token - session token
 * @Author: Egor Grishin
 */
public record LoginResponse(
        String token
) {}