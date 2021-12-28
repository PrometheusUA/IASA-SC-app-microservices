package ua.kpi.iasa.sc.identityservice.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.kpi.iasa.sc.identityservice.repository.model.Role;

import java.util.Collection;

@Data
@NoArgsConstructor
@AllArgsConstructor
public final class UserAdminDTO {
    private boolean blocked;
    private boolean deleted;
    private boolean confirmed;
    private Collection<Role> roles;
}
