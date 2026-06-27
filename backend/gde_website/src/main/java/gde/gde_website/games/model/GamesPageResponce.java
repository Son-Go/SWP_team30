package gde.gde_website.games.model;

import java.util.List;

/**
 * Response body for a single game item in paginated games list.
 *
 * @param id - game id
 * @param authorId - id of game author
 * @param title - game title
 * @param description - game description
 * @param bannerUrl - game banner url
 * @param author - author information, if available
 * @param tags - list of tag names linked with the game
 */
public record GamesPageResponce(
        Long id,
        Long authorId,
        String title,
        String description,
        String bannerUrl,
        AuthorResponse author,
        List<String> tags
) {
}
