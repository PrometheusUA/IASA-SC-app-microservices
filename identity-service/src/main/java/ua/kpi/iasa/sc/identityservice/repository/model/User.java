package ua.kpi.iasa.sc.identityservice.repository.model;

import ua.kpi.iasa.sc.identityservice.api.dto.UserDTO;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;

@Entity
@Table(name = "users", indexes = {
        @Index(name="email_id", columnList = "email"),
        @Index(name="pib_id", columnList = "surname, name, patronymic", unique = true)
})
public final class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Column(nullable = false)
    private String password_hashed;
    @Column(unique = true, nullable = false, length = 100)
    private String email;
    @Column(nullable = false, length = 40)
    private String name;
    @Column(nullable = false, length = 40)
    private String surname;
    @Column(length = 40)
    private String patronymic;
    @Column(nullable = false)
    private boolean blocked;
    @Column(nullable = false)
    private boolean deleted;
    @Column(nullable = false)
    private boolean confirmed;
    @Column(length = 255)
    private String docPhoto;
    @Column(nullable = false)
    private Timestamp createdOn;
    @Column(nullable = false)
    private boolean emailConfirmed;
    @ManyToMany(fetch = FetchType.EAGER)
    private Collection<Role> roles = new ArrayList<Role>();

    public User() {
    }

    public User(String password_hashed, String email, String name, String surname, String patronymic, boolean blocked, boolean deleted, boolean confirmed, String docPhoto, Timestamp createdOn, boolean emailConfirmed) {
        this.password_hashed = password_hashed;
        setEmail(email);
        this.name = name;
        this.surname = surname;
        this.patronymic = patronymic;
        this.blocked = blocked;
        this.deleted = deleted;
        this.confirmed = confirmed;
        this.docPhoto = docPhoto;
        this.createdOn = createdOn;
        this.emailConfirmed = emailConfirmed;
    }

    public User(UserDTO userDTO, Role studrole) {
        this.password_hashed = userDTO.getPassword();
        setEmail(userDTO.getEmail());
        this.name = userDTO.getName();
        this.surname = userDTO.getSurname();
        this.patronymic = userDTO.getPatronymic();
        this.blocked = false;
        this.deleted = false;
        this.confirmed = false;
        this.emailConfirmed = false;
        this.createdOn = new Timestamp(System.currentTimeMillis());
        this.docPhoto = userDTO.getDocPhoto();
        roles.add(studrole);
    }

    public User(UserDTO userDTO) {
        this.password_hashed = userDTO.getPassword();
        setEmail(userDTO.getEmail());
        this.name = userDTO.getName();
        this.surname = userDTO.getSurname();
        this.patronymic = userDTO.getPatronymic();
        this.blocked = false;
        this.deleted = false;
        this.confirmed = false;
        this.emailConfirmed = false;
        this.createdOn = new Timestamp(System.currentTimeMillis());
        this.docPhoto = userDTO.getDocPhoto();
        this.roles = userDTO.getRoles();
    }

    public long getId() {
        return id;
    }

    public String getPassword_hashed() {
        return password_hashed;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getPatronymic() {
        return patronymic;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setPassword_hashed(String password_hashed) {
        this.password_hashed = password_hashed;
    }

    public void setEmail(String email) {
        String emailRegex = "^(?=.{1,64}@)[\\p{L}0-9_-]+(\\.[\\p{L}0-9_-]+)*@"
                + "[^-][\\p{L}0-9-]+(\\.[\\p{L}0-9-]+)*(\\.[\\p{L}]{2,})$";
        if(!email.matches(emailRegex))
            throw new IllegalArgumentException("email is in inappropriate format");
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public void addRole(Role newRole) {
        if (roles.contains(newRole))
            throw new IllegalArgumentException("User allready had this role");
        roles.add(newRole);
    }

    public Collection<Role> getRoles() {
        return roles;
    }

    public void setRoles(Collection<Role> roles) {
        this.roles = roles;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    public String getDocPhoto() {
        return docPhoto;
    }

    public void setDocPhoto(String docPhoto) {
        this.docPhoto = docPhoto;
    }

    public Timestamp getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Timestamp createdOn) {
        this.createdOn = createdOn;
    }

    public boolean isEmailConfirmed() {
        return emailConfirmed;
    }

    public void setEmailConfirmed(boolean emailConfirmed) {
        this.emailConfirmed = emailConfirmed;
    }
}
