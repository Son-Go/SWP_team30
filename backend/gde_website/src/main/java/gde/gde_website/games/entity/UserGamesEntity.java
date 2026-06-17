package gde.gde_website.games.entity;

import gde.gde_website.users.entity.UserEntity;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "user_games")
@IdClass(UserGamesId.class)
public class UserGamesEntity {

    @Id
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Id
    @Column(name = "game_id", nullable = false)
    private Long gameId;

    @Column(name = "added_at", nullable = false, columnDefinition = "TIMESTAMPTZ DEFAULT now()")
    private Instant addedAt;

    @Column(length = 20)
    private String status = "OWNED";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false,
            foreignKey = @ForeignKey(name = "fk_usergames_user"))
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", insertable = false, updatable = false,
            foreignKey = @ForeignKey(name = "fk_usergames_game"))
    private GamesEntity game;

    public UserGamesEntity() {
    }

    public UserGamesEntity(Long userId, Long gameId) {
        this.userId = userId;
        this.gameId = gameId;
    }

    @PrePersist
    protected void onCreate() {
        if (addedAt == null) {
            addedAt = Instant.now();
        }
    }

    public Long getUserId() {
        return userId;
    }

    public Long getGameId() {
        return gameId;
    }

    public Instant getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(Instant addedAt) {
        this.addedAt = addedAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public UserEntity getUser() {
        return user;
    }

    public GamesEntity getGame() {
        return game;
    }
}
