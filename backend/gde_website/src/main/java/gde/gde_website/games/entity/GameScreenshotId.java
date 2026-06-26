package gde.gde_website.games.entity;

import java.io.Serializable;
import java.util.Objects;

public class GameScreenshotId implements Serializable {

    private Long gameId;
    private String screenshotUrl;

    public GameScreenshotId() {
    }

    public GameScreenshotId(Long gameId, String screenshotUrl) {
        this.gameId = gameId;
        this.screenshotUrl = screenshotUrl;
    }

    public Long getGameId() {
        return gameId;
    }

    public String getScreenshotUrl() {
        return screenshotUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GameScreenshotId)) return false;
        GameScreenshotId that = (GameScreenshotId) o;
        return Objects.equals(gameId, that.gameId) && Objects.equals(screenshotUrl, that.screenshotUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameId, screenshotUrl);
    }
}
