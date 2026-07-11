package gde.gde_website.games.entity;

import gde.gde_website.users.entity.UserEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/**
 * Persistent comment entity linked to a game and a user.
 * Stores scalar foreign keys together with read-only entity relations,
 * comment text and audit timestamps.
 *
 * @Author: Egor Grishin
 */
@Entity
@Table(name = "comment")
@Getter
@Setter
@NoArgsConstructor
public class CommentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private UserEntity user;

    @Column(name = "game_id", nullable = false)
    private Long gameId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", insertable = false, updatable = false)
    private GamesEntity game;

    @Column(nullable = false, columnDefinition = "text")
    private String text;

    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMPTZ DEFAULT now()")
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false, columnDefinition = "TIMESTAMPTZ DEFAULT now()")
    private Instant updatedAt;

    /**
     * Creates a new comment entity with scalar foreign keys and text.
     * Audit timestamps are filled automatically by entity lifecycle callbacks.
     *
     * @param userId - id of comment author
     * @param gameId - id of game that owns the comment
     * @param text - comment text
     */
    public CommentEntity(Long userId, Long gameId, String text) {
        this.userId = userId;
        this.gameId = gameId;
        this.text = text;
    }

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        if (createdAt == null) {
            createdAt = now;
        }
        if (updatedAt == null) {
            updatedAt = now;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}
