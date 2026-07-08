package gde.gde_website.users.model;

/**
 * Response representing a user's public profile, visible to any visitor.
 * Contains only non-sensitive information.
 * @param id - user id
 * @param username - username of the user
 * @param profileImageUrl - profile image URL of the user, or null if not set
 * @param gameCount - total number of games created by this user
 * @Author: Artemii Gorelov
 */
public record PublicUserProfileResponse(
        Long id,
        String username,
        String profileImageUrl,
        Long gameCount
) {
}
