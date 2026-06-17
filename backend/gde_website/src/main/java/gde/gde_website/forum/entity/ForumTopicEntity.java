package gde.gde_website.forum.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "forum_topic")
@Getter
@Setter
@NoArgsConstructor
public class ForumTopicEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @Column(name = "category_id", nullable = false)
    private Integer categoryId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(length = 255, nullable = false)
    private String title;

    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMPTZ DEFAULT now()")
    private Instant createdAt;

    public ForumTopicEntity(Integer categoryId, Long userId, String title) {
        this.categoryId = categoryId;
        this.userId = userId;
        this.title = title;
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }
}
