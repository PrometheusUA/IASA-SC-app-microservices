package ua.kpi.iasa.sc.identityservice.api.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ua.kpi.iasa.sc.identityservice.repository.model.User;

@Data
@NoArgsConstructor
public class UserBackShortDTO {
    public UserBackShortDTO(User user){
        this.id = user.getId();
        this.fullname = user.getSurname() + " " + user.getName() + (user.getPatronymic()==null?"":(" " + user.getPatronymic()));
    }

    private long id;
    private String fullname;
}
