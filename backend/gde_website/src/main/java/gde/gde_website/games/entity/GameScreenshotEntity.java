package gde.gde_website.games.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "game_screenshot")
@IdClass(GameScreenshotId.class)
@Getter
@Setter
@NoArgsConstructor
public class GameScreenshotEntity {
    @Id
    @Column(name = "game_id", nullable = false)
    @Setter(AccessLevel.NONE)
    private Long gameId;

    @Id
    @Column(name = "screenshot_url", nullable = false, length = 500)
    private String screenshotUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", insertable = false, updatable = false,
            foreignKey = @ForeignKey(name = "fk_gamescreenshot_game"))
    private GamesEntity game;

    public GameScreenshotEntity(Long gameId, String screenshotUrl) {
        this.gameId = gameId;
        this.screenshotUrl = screenshotUrl;
    }
}
