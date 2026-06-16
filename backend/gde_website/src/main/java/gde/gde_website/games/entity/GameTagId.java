package gde.gde_website.games.model;

import java.io.Serializable;
import java.util.Objects;

public class GameTagId implements Serializable {

    private Long gameId;
    private Integer tagId;

    public GameTagId() {
    }

    public GameTagId(Long gameId, Integer tagId) {
        this.gameId = gameId;
        this.tagId = tagId;
    }

    public Long getGameId() {
        return gameId;
    }

    public Integer getTagId() {
        return tagId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GameTagId)) return false;
        GameTagId that = (GameTagId) o;
        return Objects.equals(gameId, that.gameId) && Objects.equals(tagId, that.tagId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameId, tagId);
    }
}
