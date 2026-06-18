package gde.gde_website.forum.model;

import java.time.Instant;

public record ForumPost(
        Long id,
        Long topicId,
        Long userId,
        String content,
        Instant createdAt
) {
}
