package gde.gde_website.users.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "username"),
        @UniqueConstraint(columnNames = "email")
})
@Getter
@Setter
@NoArgsConstructor
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @Column(length = 50, nullable = false)
    private String username;

    @Column(length = 255, nullable = false)
    private String email;

    @Column(name = "profile_image_url", length = 500)
    private String profileImageUrl;

    @Column(length = 255, nullable = false)
    private String passwordHash;

    @Column(nullable = false, columnDefinition = "TIMESTAMPTZ DEFAULT now()")
    private Instant createdAt;

    public UserEntity(String username, String email, String passwordHash, String profileImageUrl) {
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.profileImageUrl = profileImageUrl;
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }
}
