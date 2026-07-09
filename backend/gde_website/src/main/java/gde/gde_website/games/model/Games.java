package gde.gde_website.games.model;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * This is a record which represents game info
 * @param id - game id
 * @param authorId - game author id
 * @param title - game title
 * @param shortDescription - short description displayed on game cards
 * @param description - game description
 * @param bannerUrl - game banner path
 * @param createdAt - date in which game was created
 * @param updatedAt - date in which game was updated
 * @param gameTags - game tags grouped by tag type
 * @param screenshots - screenshots grouped into {@code videos} and {@code pictures} lists
 * @Author: Artemii Gorelov, Egor Grishin
 */
public record Games(
        Long id,
        Long authorId,
        String title,
        String shortDescription,
        String description,
        String bannerUrl,
        Instant createdAt,
        Instant updatedAt,
        Map<String, List<String>> gameTags,
        Map<String, List<String>> screenshots
) {
}
