package ua.kpi.iasa.sc.identityservice.repository.model;

import javax.persistence.*;

@Entity
@Table(indexes = {
        @Index(name="indexRoleName", columnList = "roleName")
})
public final class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long roleId;
    @Column(nullable = false, length = 20, unique = true)
    private String roleName;

    public Role(long roleId, String roleName) {
        this.roleId = roleId;
        this.roleName = roleName;
    }

    public Role(String roleName) {
        this.roleName = roleName;
    }

    public Role() {
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public long getRoleId() {
        return roleId;
    }
}
