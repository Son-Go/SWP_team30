package gde.gde_website.users.model;

/**
 * Request body used for changing current user's password.
 * @param oldPassword - user old password
 * @param newPassword - user password after updating
 * @Author: Artemii Gorelov
 */
public record ChangePasswordRequest(
        String oldPassword,
        String newPassword
) {
}