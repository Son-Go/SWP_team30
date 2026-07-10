package gde.gde_website.games.model;

import java.util.List;
import java.util.Map;

/**
 * Response body for a single game item in paginated games list.
 *
 * @param id - game id
 * @param authorId - id of game author
 * @param title - game title
 * @param shortDescription - short description displayed on game cards
 * @param description - game description
 * @param bannerUrl - game banner url
 * @param author - author information, if available
 * @param gameTags - tag names grouped by tag type; every key is a tag type name and every value is a list of tag names of that type
 */
public record GamesPageResponse(
        Long id,
        Long authorId,
        String title,
        String shortDescription,
        String description,
        String bannerUrl,
        AuthorResponse author,
        boolean isApproved,
        Map<String, List<String>> gameTags,
        List<String> pictures
) {
}
