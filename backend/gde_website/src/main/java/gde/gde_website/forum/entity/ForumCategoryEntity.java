package gde.gde_website.forum.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "forum_category")
@Getter
@Setter
@NoArgsConstructor
public class ForumCategoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Integer id;

    @Column(length = 255, nullable = false)
    private String title;

    @Column(columnDefinition = "text")
    private String description;

    public ForumCategoryEntity(String title, String description) {
        this.title = title;
        this.description = description;
    }
}
