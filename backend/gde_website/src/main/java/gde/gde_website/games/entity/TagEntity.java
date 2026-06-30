package gde.gde_website.games.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tag", uniqueConstraints = {
        @UniqueConstraint(columnNames = "name")
})
@Getter
@Setter
@NoArgsConstructor
public class TagEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Integer id;

    @Column(length = 50, nullable = false)
    private String name;

    @Column(name = "tag_type_id", nullable = false)
    private Integer tagTypeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_type_id", insertable = false, updatable = false,
            foreignKey = @ForeignKey(name = "fk_tag_tagtype"))
    private TagTypeEntity tagType;

    public TagEntity(String name) {
        this(name, 1);
    }

    public TagEntity(String name, Integer tagTypeId) {
        this.name = name;
        this.tagTypeId = tagTypeId;
    }
}
