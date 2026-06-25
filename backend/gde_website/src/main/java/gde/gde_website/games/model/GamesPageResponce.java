package gde.gde_website.games.model;

import java.util.List;

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
