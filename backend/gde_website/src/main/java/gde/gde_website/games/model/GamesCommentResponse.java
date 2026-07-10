package gde.gde_website.games.model;

import java.time.Instant;

public record GamesCommentResponse(
        Long id,
        AuthorResponse author,
        String text,
        Instant createdAt,
        Instant updatedAt
) {
}
