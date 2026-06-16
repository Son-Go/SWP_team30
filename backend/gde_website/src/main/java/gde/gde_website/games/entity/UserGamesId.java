package gde.gde_website.games.model;

import java.io.Serializable;
import java.util.Objects;

public class UserGamesId implements Serializable {

    private Long userId;
    private Long gameId;

    public UserGamesId() {
    }

    public UserGamesId(Long userId, Long gameId) {
        this.userId = userId;
        this.gameId = gameId;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getGameId() {
        return gameId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserGamesId)) return false;
        UserGamesId that = (UserGamesId) o;
        return Objects.equals(userId, that.userId) && Objects.equals(gameId, that.gameId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, gameId);
    }
}
