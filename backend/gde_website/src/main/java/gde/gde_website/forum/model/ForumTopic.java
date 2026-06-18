package gde.gde_website.forum.model;

import java.time.Instant;

public record ForumTopic(
        Long id,
        Integer categoryId,
        Long userId,
        String title,
        Instant createdAt
) {
}
