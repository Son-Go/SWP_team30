package gde.gde_website.games.model;

import java.util.List;

public record GamesCardResponce(
        Long id,
        String title,
        String description,
        String bannerUrl,
        List<String> tags
) {
}
