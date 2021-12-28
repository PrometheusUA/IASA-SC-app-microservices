package ua.kpi.iasa.sc.identityservice;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import ua.kpi.iasa.sc.identityservice.api.dto.UserAdminDTO;
import ua.kpi.iasa.sc.identityservice.api.dto.UserDTO;
import ua.kpi.iasa.sc.identityservice.repository.model.Role;
import ua.kpi.iasa.sc.identityservice.service.UserService;

import java.util.*;

@SpringBootApplication
public class IdentityServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(IdentityServiceApplication.class, args);
    }

    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    CommandLineRunner run(UserService userService){
        return args -> {
            try{
                userService.createRole("Student");
                userService.createRole("Teacher");
                userService.createRole("Elderly");
                userService.createRole("Admin");
                List<Role> allRoles = userService.fetchAllRoles();
                long id =userService.create(new UserDTO("pass", "admin@admin.com", "Admin", "Cool", null, null, allRoles));
                userService.update(id, new UserAdminDTO(false, false, true, null));
            }
            catch (Exception e){}
        };
    }
}
