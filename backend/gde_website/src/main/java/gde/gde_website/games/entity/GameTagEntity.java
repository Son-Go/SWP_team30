package gde.gde_website.games.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "game_tag")
@IdClass(GameTagId.class)
public class GameTagEntity {

    @Id
    @Column(name = "game_id", nullable = false)
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

    public GameTagEntity() {
    }

    public GameTagEntity(Long gameId, Integer tagId) {
        this.gameId = gameId;
        this.tagId = tagId;
    }

    public Long getGameId() {
        return gameId;
    }

    public Integer getTagId() {
        return tagId;
    }

    public GamesEntity getGame() {
        return game;
    }

    public TagEntity getTag() {
        return tag;
    }
}
