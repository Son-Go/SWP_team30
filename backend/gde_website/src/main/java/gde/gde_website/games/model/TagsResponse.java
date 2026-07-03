package gde.gde_website.games.model;

import java.util.List;
import java.util.Map;

/**
 * Response body containing all available tags grouped by tag type.
 *
 * @param gameTags - ordered map where key is tag type name and value is list of all tag names of that type
 * @Author: Egor Grishin
 */
public record TagsResponse(
        Map<String, List<String>> gameTags
) {
}
