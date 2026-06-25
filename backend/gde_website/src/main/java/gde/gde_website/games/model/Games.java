package gde.gde_website.games.model;

import java.time.Instant;

/**
 * This is a record which represents game info
 * @param id - game id
 * @param authorId - game author id
 * @param title - game title
 * @param description - game description
 * @param bannerUrl - game banner path
 * @param createdAt - date in which game was created
 * @param updatedAt - date in which game was updated
 * @Author: Artemii Gorelov
 */
public record Games(
        Long id,
        Long authorId,
        String title,
        String description,
        String bannerUrl,
        Instant createdAt,
        Instant updatedAt
) {
}
