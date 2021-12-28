package ua.kpi.iasa.sc.identityservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ua.kpi.iasa.sc.identityservice.repository.model.Role;
import ua.kpi.iasa.sc.identityservice.repository.model.User;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {
    @Query("SELECT u FROM User u WHERE u.blocked = true")
    List<User> findAllBlocked();

    @Query("SELECT u FROM User u WHERE u.deleted = true")
    List<User> findAllDeleted();

    @Query("SELECT u FROM User u WHERE u.confirmed = false")
    List<User> findAllUnconfirmed();

    @Query("SELECT u FROM User u WHERE u.blocked = false AND u.deleted = false AND u.confirmed = true")
    List<User> findAllNormal();

    Optional<User> findByEmail(String email);

    List<User> findByRolesContaining(Role role);

    List<User> findByIdIn(List<Long> ids);
}
