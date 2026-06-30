package gde.gde_website.games.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tag_type", uniqueConstraints = {
        @UniqueConstraint(columnNames = "type")
})
@Getter
@Setter
@NoArgsConstructor
public class TagTypeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Integer id;

    @Column(length = 50, nullable = false)
    private String type;

    public TagTypeEntity(String type) {
        this.type = type;
    }
}
