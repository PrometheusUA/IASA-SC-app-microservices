package ua.kpi.iasa.sc.identityservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ua.kpi.iasa.sc.identityservice.api.dto.UserAdminDTO;
import ua.kpi.iasa.sc.identityservice.api.dto.UserDTO;
import ua.kpi.iasa.sc.identityservice.repository.RoleRepo;
import ua.kpi.iasa.sc.identityservice.repository.UserRepo;
import ua.kpi.iasa.sc.identityservice.repository.model.Role;

import java.util.*;

@RequiredArgsConstructor
@Service
public final class UserService implements UserDetailsService {

    private final UserRepo userRepo;
    private final RoleRepo roleRepo;
    private final PasswordEncoder passwordEncoder;

    public List<ua.kpi.iasa.sc.identityservice.repository.model.User> fetchAll() {
        return userRepo.findAll();
    }

    public List<ua.kpi.iasa.sc.identityservice.repository.model.User> fetchDeleted() {
        return userRepo.findAllDeleted();
    }

    public List<ua.kpi.iasa.sc.identityservice.repository.model.User> fetchBlocked() {
        return userRepo.findAllBlocked();
    }

    public List<ua.kpi.iasa.sc.identityservice.repository.model.User> fetchUnconfirmed() {
        return userRepo.findAllUnconfirmed();
    }

    public List<ua.kpi.iasa.sc.identityservice.repository.model.User> fetchNormal() {
        return userRepo.findAllNormal();
    }

    public List<ua.kpi.iasa.sc.identityservice.repository.model.User> fetchAdmins() {
        Optional<Role> foundRole = roleRepo.findByRoleName("Admin");
        Role adminRole;
        if(foundRole.isEmpty())
            adminRole = roleRepo.save(new Role("Admin"));
        else
            adminRole = foundRole.get();
        return userRepo.findByRolesContaining(adminRole);
    }

    public List<ua.kpi.iasa.sc.identityservice.repository.model.User> fetchByIdIn(List<Long> ids) {
        return userRepo.findByIdIn(ids);
    }

    public ua.kpi.iasa.sc.identityservice.repository.model.User fetchById(long id) {
        final Optional<ua.kpi.iasa.sc.identityservice.repository.model.User> foundUser = userRepo.findById(id);
        if (foundUser.isEmpty())
            throw new IllegalArgumentException("User not found");
        return foundUser.get();
    }

    public ua.kpi.iasa.sc.identityservice.repository.model.User fetchByEmail(String email) {
        final Optional<ua.kpi.iasa.sc.identityservice.repository.model.User> foundUser = userRepo.findByEmail(email);
        if (foundUser.isEmpty())
            throw new IllegalArgumentException("User not found");
        return foundUser.get();
    }

    public long create(UserDTO userdto) {
        ua.kpi.iasa.sc.identityservice.repository.model.User savedUser;
        userdto.setPassword(passwordEncoder.encode(userdto.getPassword()));
        userdto.setEmail(userdto.getEmail().toLowerCase(Locale.ROOT));
        if (userdto.getRoles() == null || userdto.getRoles().isEmpty()) {
            final Optional<Role> foundStudrole = roleRepo.findByRoleName("Student");
            Role studrole;
            if (foundStudrole.isEmpty())
                studrole = roleRepo.save(new Role("Student"));
            else
                studrole = foundStudrole.get();
            final ua.kpi.iasa.sc.identityservice.repository.model.User user = new ua.kpi.iasa.sc.identityservice.repository.model.User(userdto, studrole);
            savedUser = userRepo.save(user);
        }
        else{
            final ua.kpi.iasa.sc.identityservice.repository.model.User user = new ua.kpi.iasa.sc.identityservice.repository.model.User(userdto);
            savedUser = userRepo.save(user);
        }
        return savedUser.getId();
    }

    public void addRoleToUser(long id, String roleName){
        final Optional<Role> foundRole = roleRepo.findByRoleName(roleName);
        Role addedRole;
        if (foundRole.isEmpty())
            addedRole = roleRepo.save(new Role(roleName));
        else
            addedRole = foundRole.get();
        final ua.kpi.iasa.sc.identityservice.repository.model.User userToAddRole = fetchById(id);
        userToAddRole.addRole(addedRole);
    }

    public void update(long id, UserDTO user) throws IllegalAccessException{
        final ua.kpi.iasa.sc.identityservice.repository.model.User foundUser = fetchById(id);

        if (user.getEmail() != null && !user.getEmail().isBlank() && !user.getEmail().equals(foundUser.getEmail()))
            throw new IllegalAccessException("Email can't be changed");
        // add hashing
        if (user.getPassword() != null && !user.getPassword().isBlank()) foundUser.setPassword_hashed(user.getPassword());
        if (user.getName() != null && !user.getName().isBlank()) foundUser.setName(user.getName());
        if (user.getSurname() != null && !user.getSurname().isBlank()) foundUser.setSurname(user.getSurname());
        if (user.getPatronymic() != null && !user.getPatronymic().isBlank()) foundUser.setPatronymic(user.getPatronymic());
        if (user.getDocPhoto() != null && !user.getPatronymic().isBlank()) foundUser.setDocPhoto(user.getDocPhoto());
        if (user.getRoles() != null && !user.getRoles().equals(foundUser.getRoles())) foundUser.setRoles(user.getRoles());

        userRepo.save(foundUser);
    }

    public void update(long id, UserAdminDTO user) throws IllegalAccessException{
        final ua.kpi.iasa.sc.identityservice.repository.model.User foundUser = fetchById(id);

        if (user.isBlocked() != foundUser.isBlocked()) foundUser.setBlocked(user.isBlocked());
        if (user.isDeleted() != foundUser.isDeleted()) foundUser.setDeleted(user.isDeleted());
        if (user.isConfirmed() != foundUser.isConfirmed()) foundUser.setConfirmed(user.isConfirmed());
        if (user.getRoles() != null && !user.getRoles().equals(foundUser.getRoles())) foundUser.setRoles(user.getRoles());

        userRepo.save(foundUser);
    }

    public void delete(long id) {
        userRepo.deleteById(id);
    }

    public List<Role> fetchAllRoles() {
        return roleRepo.findAll();
    }

    public Role fetchRoleById(long id) {
        final Optional<Role> foundRole = roleRepo.findById(id);
        if (foundRole.isEmpty())
            throw new IllegalArgumentException("User not found");
        return foundRole.get();
    }

    public long createRole(String roleName) {
        final Role role = new Role(roleName);
        final Role savedRole = roleRepo.save(role);
        return savedRole.getRoleId();
    }

    public void updateRole(long id, String name) throws IllegalAccessException{
        final Role foundRole = fetchRoleById(id);

        // add hashing
        if (name != null && !name.isBlank()) foundRole.setRoleName(name);

        roleRepo.save(foundRole);
    }

    public void deleteRole(long id) {
        roleRepo.deleteById(id);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<ua.kpi.iasa.sc.identityservice.repository.model.User> founduser = userRepo.findByEmail(username);
        if (founduser.isEmpty()){
            throw new UsernameNotFoundException("There isn't user with such email");
        }
        else{
            ua.kpi.iasa.sc.identityservice.repository.model.User user = founduser.get();
            Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
            user.getRoles().forEach(role -> {authorities.add(new SimpleGrantedAuthority(role.getRoleName()));});
            return new User(user.getEmail(), user.getPassword_hashed(), authorities);
        }
    }
}
