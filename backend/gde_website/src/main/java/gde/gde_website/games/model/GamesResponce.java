package gde.gde_website.games.model;

import java.time.Instant;

/**
 * This is a record which represent game response
 * @param id - game id
 * @param authorId - author of the game id
 * @param title - title of the game
 * @param description - description of the game
 * @param bannerUrl - game banner path
 * @param createdAt - date at which game was created
 * @param updatedAt - date at which game was updated
 * @param isOwner - represents status of user
 * @Author: Egor Grishin
 */
public record GamesResponce(
        Long id,
        Long authorId,
        String title,
        String description,
        String bannerUrl,
        Instant createdAt,
        Instant updatedAt,
        boolean isOwner
) {
}
