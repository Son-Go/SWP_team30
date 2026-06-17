package gde.gde_website.games.entity;

import gde.gde_website.users.entity.UserEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "user_games")
@IdClass(UserGamesId.class)
@Getter
@Setter
@NoArgsConstructor
public class UserGamesEntity {
    @Id
    @Column(name = "user_id", nullable = false)
    @Setter(AccessLevel.NONE)
    private Long userId;

    @Id
    @Column(name = "game_id", nullable = false)
    @Setter(AccessLevel.NONE)
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
}
