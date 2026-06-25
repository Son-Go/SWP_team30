package gde.gde_website.games.model;

import java.time.Instant;

/**
 * This is a record which represents users games
 * @param userId - id of game owner
 * @param gameId - id of game
 * @param addedAt - date in which game was added
 * @param status - game addition status
 * @Author: Artemii Gorelov
 */
public record UserGames(
        Long userId,
        Long gameId,
        Instant addedAt,
        GameStatus status
) {
}
