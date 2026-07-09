package gde.gde_website.games.model;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Response body for a detailed game card.
 *
 * @param id - game id
 * @param authorId - game author id
 * @param title - game title
 * @param shortDescription - short description displayed on game cards
 * @param description - game description
 * @param bannerUrl - game banner path
 * @param createdAt - date at which game was created
 * @param updatedAt - date at which game was updated
 * @param isOwner - whether current authenticated user is the owner of the game
 * @param author - author information, if available
 * @param gameTags - tag names grouped by tag type; every key is a tag type name and every value is a list of tag names of that type
 * @param screenshots - screenshots grouped into {@code videos} and {@code pictures} lists
 * @Author: Egor Grishin
 */
public record GamesCardResponse(
        Long id,
        Long authorId,
        String title,
        String shortDescription,
        String description,
        String shortDescription,
        String bannerUrl,
        Instant createdAt,
        Instant updatedAt,
        boolean isOwner,
        boolean isApproved,
        AuthorResponse author,
        Map<String, List<String>> gameTags,
        Map<String, List<String>> screenshots
) {
}
