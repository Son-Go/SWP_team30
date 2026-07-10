package gde.gde_website.games.model;

import jakarta.validation.constraints.NotBlank;

public record GamesCreateCommentRequest(
        @NotBlank
        String text
) {
}
