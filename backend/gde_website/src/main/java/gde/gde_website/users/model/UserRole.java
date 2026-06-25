package gde.gde_website.users.model;

/**
 * This is a record for User role
 * @param userId - id of user
 * @param roleId - id of specific role
 *
 * @Author: Artemii Gorelov
 */
public record UserRole(
        Long userId,
        Integer roleId
) {
}
