package ua.kpi.iasa.sc.identityservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.kpi.iasa.sc.identityservice.repository.model.Role;

import java.util.Optional;

@Repository
public interface RoleRepo extends JpaRepository<Role, Long> {
    Optional<Role> findByRoleName (String roleName);
}
