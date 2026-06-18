package gde.gde_website.games.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "game_tag")
@IdClass(GameTagId.class)
@Getter
@Setter
@NoArgsConstructor
public class GameTagEntity {
    @Id
    @Column(name = "game_id", nullable = false)
    @Setter(AccessLevel.NONE)
    private Long gameId;

    @Id
    @Column(name = "tag_id", nullable = false)
    private Integer tagId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", insertable = false, updatable = false,
            foreignKey = @ForeignKey(name = "fk_gametag_game"))
    private GamesEntity game;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id", insertable = false, updatable = false,
            foreignKey = @ForeignKey(name = "fk_gametag_tag"))
    private TagEntity tag;

    public GameTagEntity(Long gameId, Integer tagId) {
        this.gameId = gameId;
        this.tagId = tagId;
    }
}
