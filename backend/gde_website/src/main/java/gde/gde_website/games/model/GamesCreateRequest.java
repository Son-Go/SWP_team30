package gde.gde_website.games.model;

import java.util.List;

/**
 * Request body for creating a new game.
 * Contains game fields and an optional list of tag names.
 *
 * @param title - game title
 * @param description - game description
 * @param bannerUrl - banner url of the game
 * @param gameTags - optional list of tag names to be linked with the game
 */
public record GamesCreateRequest(
        String title,
        String description,
        String bannerUrl,
        List<String> gameTags
) {
}
