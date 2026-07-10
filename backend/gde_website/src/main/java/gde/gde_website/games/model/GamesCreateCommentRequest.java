package gde.gde_website.games.model;

import jakarta.validation.constraints.NotBlank;

/**
 * Request body for creating or updating a game comment.
 * Current validation requires comment text to be non-blank.
 *
 * @param text - comment text submitted by client
 * @Author: Egor Grishin
 */
public record GamesCreateCommentRequest(
        @NotBlank
        String text
) {
}
