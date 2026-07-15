package gde.gde_website.games.model;

import java.time.Instant;
import java.util.List;

public record AdminGamesListResponse(List<AdminGameInfo> games) {
    public record AdminGameInfo(
            Long id,
            String title,
            String shortDescription,
            String bannerUrl,
            Boolean approved,
            Boolean hidden,
            Instant createdAt,
            AuthorInfo author
    ) {
    }

    public record AuthorInfo(
            Long id,
            String username,
            String profileImageUrl
    ) {
    }
}