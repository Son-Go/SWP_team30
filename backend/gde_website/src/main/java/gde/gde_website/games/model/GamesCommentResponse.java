package gde.gde_website.games.model;

import java.time.Instant;

/**
 * Response body for a single game comment.
 *
 * @param id - comment id
 * @param author - original comment author information
 * @param text - comment text
 * @param createdAt - timestamp when comment was created
 * @param updatedAt - timestamp when comment was last updated
 * @Author: Egor Grishin
 */
public record GamesCommentResponse(
        Long id,
        AuthorResponse author,
        String text,
        Instant createdAt,
        Instant updatedAt
) {
}
