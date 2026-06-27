package gde.gde_website.games.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "game_screenshots")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GamesScreenshotEntity {
    @Id
    @Setter(AccessLevel.NONE)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "game_id", nullable = false)
    private Long gameId;

    @Column(name = "url", nullable = false, length = 2000)
    private String url;

    public GamesScreenshotEntity(Long gameId, String url) {
        this.gameId = gameId;
        this.url = url;
    }
}