package gde.gde_website.users.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_role")
@IdClass(UserRoleId.class)
@Getter
@Setter
@NoArgsConstructor
public class UserRoleEntity {
    @Id
    @Column(name = "user_id", nullable = false)
    @Setter(AccessLevel.NONE)
    private Long userId;

    @Id
    @Column(name = "role_id", nullable = false)
    private Integer roleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false,
            foreignKey = @ForeignKey(name = "fk_userrole_user"))
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", insertable = false, updatable = false,
            foreignKey = @ForeignKey(name = "fk_userrole_role"))
    private RoleEntity role;

    public UserRoleEntity(Long userId, Integer roleId) {
        this.userId = userId;
        this.roleId = roleId;
    }
}
