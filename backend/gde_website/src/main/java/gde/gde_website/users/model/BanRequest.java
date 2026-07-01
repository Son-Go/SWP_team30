package gde.gde_website.users.model;

/**
 * This record is representing user to be banned
 * @param userId - id of user to be banned
 * @Author: Artemii Gorelov
 */
public record BanRequest(
        Long userId
) {
}
