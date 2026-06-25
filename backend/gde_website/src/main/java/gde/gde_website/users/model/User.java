package gde.gde_website.users.model;

import java.time.Instant;

/**
 * This is a record of User
 * @param id - user id
 * @param username - user nickname
 * @param email - user email
 * @param passwordHash - password in format of hash
 * @param createdAt - date of user creation
 * @Author: Artemii Gorelov
 */
public record User(
        Long id,
        String username,
        String email,
        String passwordHash,
        Instant createdAt
) {
}
