package gde.gde_website.games.model;

import java.util.List;

/**
 * This record is used for writing tag responses with game tags
 * @param gameTags - tags of game
 * @Author: Egor Grishin
 */
public record TagsResponse(
        List<String> gameTags
) {
}
