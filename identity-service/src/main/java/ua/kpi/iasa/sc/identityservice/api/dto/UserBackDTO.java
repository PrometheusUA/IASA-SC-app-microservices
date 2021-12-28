package ua.kpi.iasa.sc.identityservice.api.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ua.kpi.iasa.sc.identityservice.repository.model.Role;
import ua.kpi.iasa.sc.identityservice.repository.model.User;

import java.util.Collection;

@Data
@NoArgsConstructor
public class UserBackDTO {
    public UserBackDTO(User user){
        this.id = user.getId();
        this.email = user.getEmail();
        this.name = user.getName();
        this.surname = user.getSurname();
        this.patronymic = user.getPatronymic();
    }

    private long id;
    private String email;
    private String name;
    private String surname;
    private String patronymic;
}
