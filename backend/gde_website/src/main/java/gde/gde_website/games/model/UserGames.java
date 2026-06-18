package gde.gde_website.games.model;

import java.time.Instant;

public record UserGames(
        Long userId,
        Long gameId,
        Instant addedAt,
        GameStatus status
) {
}
