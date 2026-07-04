package gde.gde_website.games.model;

import java.util.List;
import java.util.Map;

/**
 * Request body for updating an existing game.
 * Any field may be omitted by passing {@code null}. When {@code gameTags}
 * is provided, current tag relations are replaced with the provided list.
 *
 * @param title - new game title or {@code null}
 * @param description - new game description or {@code null}
 * @param bannerUrl - new banner url or {@code null}
 * @param gameTags - new list of tag names or {@code null}
 * @param screenshots - new screenshots grouped into {@code videos} and {@code pictures} lists, or {@code null}
 */
public record UpdateGameRequest(
        String title,
        String description,
        String bannerUrl,
        List<String> gameTags,
        Map<String, List<String>> screenshots
) {
}
