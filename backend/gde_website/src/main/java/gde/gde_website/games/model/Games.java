package gde.gde_website.games.model;

import java.time.Instant;

public record Games(
        Long id,
        Long authorId,
        String title,
        String description,
        String bannerUrl,
        Instant createdAt,
        Instant updatedAt
) {
}
