package gde.gde_website.games.model;

import java.util.List;

public record UpdateGameRequest(
        String title,
        String description,
        String bannerUrl,
        List<String> gameTags
) {
}
