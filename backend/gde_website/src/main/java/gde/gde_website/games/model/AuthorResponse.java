package gde.gde_website.games.model;

/**
 * This record is used for author response
 * @param id - author user id
 * @param username - author nickname
 * @param profile_image_url - author profile image url
 * @param email - author email
 * @Author: Artemii Gorelov
 */
public record AuthorResponse(
        Long id,
        String username,
        String profile_image_url,
        String email
) {}
