package ua.kpi.iasa.sc.identityservice.api;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.kpi.iasa.sc.identityservice.api.dto.UserAdminDTO;
import ua.kpi.iasa.sc.identityservice.api.dto.UserBackDTO;
import ua.kpi.iasa.sc.identityservice.api.dto.UserBackShortDTO;
import ua.kpi.iasa.sc.identityservice.api.dto.UserDTO;
import ua.kpi.iasa.sc.identityservice.repository.model.Role;
import ua.kpi.iasa.sc.identityservice.repository.model.User;
import ua.kpi.iasa.sc.identityservice.security.utility.TokenUtility;
import ua.kpi.iasa.sc.identityservice.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/identity")
public final class UserController {

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<?> index(@RequestParam(required = false) String type) {
        List<UserBackDTO> usersToSend = new ArrayList<>();
        List<User> validUsers;
        if (type != null){
            if (type.equals("deleted")){
                validUsers = userService.fetchDeleted();
            }
            else if (type.equals("blocked")){
                validUsers = userService.fetchBlocked();
            }
            else if(type.equals("unconfirmed")){
                validUsers = userService.fetchUnconfirmed();
            }
            else if(type.equals("normal")){
                validUsers = userService.fetchNormal();
            }
            else{
                Map<String, String> error = new HashMap<>();
                error.put("error_message", "You inserted unexisting type '" + type + "'. Valid types are: 'deleted', 'blocked', 'unconfirmed', 'normal'.");
                return ResponseEntity.status(403).body(error);
            }
        }
        else
            validUsers = userService.fetchAll();

        validUsers.forEach(user -> { usersToSend.add(new UserBackDTO(user));});
        return ResponseEntity.ok(usersToSend);
    }

    @GetMapping("/admins")
    public ResponseEntity<List<UserBackDTO>> indexAdmins() {
        List<User> validUsers = userService.fetchAdmins();
        List<UserBackDTO> usersToSend = new ArrayList<>();
        validUsers.forEach(user -> { usersToSend.add(new UserBackDTO(user));});
        return ResponseEntity.ok(usersToSend);
    }

    @PostMapping("/byids")
    public ResponseEntity<?> indexById(@RequestBody List<Long> ids){
        List<User> validUsers = userService.fetchByIdIn(ids);
        List<UserBackShortDTO> usersToSend = new ArrayList<>();
        validUsers.forEach(user -> { usersToSend.add(new UserBackShortDTO(user));});
        return ResponseEntity.ok(usersToSend);
    }

    @PostMapping("/byids/unauthorized")
    public ResponseEntity<?> indexByIdUnauth(@RequestBody List<Long> ids){
        List<User> validUsers = userService.fetchByIdIn(ids);
        List<UserBackShortDTO> usersToSend = new ArrayList<>();
        validUsers.forEach(user -> { usersToSend.add(new UserBackShortDTO(user));});
        return ResponseEntity.ok(usersToSend);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserBackDTO> profile(@PathVariable long id) {
        try {
            final User foundUser = userService.fetchById(id);
            return ResponseEntity.ok(new UserBackDTO(foundUser));
        }
        catch (IllegalArgumentException e){
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/signup")
    public void signup(HttpServletRequest request, HttpServletResponse response) throws IOException {

        try{
            StringBuffer requestBodyStr = new StringBuffer();
            String requestLine = null;
            try {
                BufferedReader reader = request.getReader();
                while ((requestLine = reader.readLine()) != null)
                    requestBodyStr.append(requestLine);
            } catch (Exception e) {
                throw new IOException("Error parsing JSON body");
            }

            JSONObject requestBody;

            try {
                String requestBodyString = requestBodyStr.toString();
                //Reader stringReader = new StringReader(requestBodyString);
                JSONParser parser = new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE);
                requestBody = (JSONObject) parser.parse(requestBodyString);
            } catch (Exception e) {
                throw new IOException("Error parsing JSON request string");
            }

            UserDTO newUser = new UserDTO();
            newUser.setEmail(requestBody.getAsString("email"));
            newUser.setPassword(requestBody.getAsString("password"));
            newUser.setName(requestBody.getAsString("name"));
            newUser.setSurname(requestBody.getAsString("surname"));
            newUser.setPatronymic(requestBody.getAsString("patronymic"));
            newUser.setDocPhoto(requestBody.getAsString("docPhoto"));

            final long id = userService.create(newUser);
            User user = userService.fetchById(id);
            TokenUtility tokenUtility = new TokenUtility(user.getEmail(), user.getId(), user.getRoles().stream().map(Role::getRoleName).collect(Collectors.toList()));

            Map<String, String> tokens = tokenUtility.generateTokens(request.getRequestURI());
            response.setContentType(APPLICATION_JSON_VALUE);
            new ObjectMapper().writeValue(response.getOutputStream(), tokens);
        }
        catch (Exception e){
            response.setStatus(FORBIDDEN.value());
            response.setHeader("error", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error_message", e.getMessage());
            response.setContentType(APPLICATION_JSON_VALUE);
            new ObjectMapper().writeValue(response.getOutputStream(), error);
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> adminUpdate(@PathVariable long id, @RequestBody UserAdminDTO user) {
        try {
            userService.update(id, user);

            return ResponseEntity.noContent().build();
        }
        catch (IllegalArgumentException e){
            return ResponseEntity.notFound().build();
        }
        catch (IllegalAccessException e){
            return ResponseEntity.status(403).build();
        }
    }

    @PatchMapping
    public ResponseEntity<Void> update(@RequestHeader String authorization, @RequestBody UserDTO user) {
        try{
            DecodedJWT decodedJWT = TokenUtility.verifyToken(authorization);
            String email = decodedJWT.getSubject();
            User oldUser = userService.fetchByEmail(email);
            if (!user.getRoles().equals(oldUser.getRoles())){
                throw new IllegalAccessException("Student can't change roles!");
            }
            userService.update(oldUser.getId(), user);

            return ResponseEntity.noContent().build();
        }
        catch (IllegalArgumentException e){
            return ResponseEntity.notFound().build();
        }
        catch (IllegalAccessException e){
            return ResponseEntity.status(403).build();
        }
    }

    @PostMapping("/delete")
    public ResponseEntity<?> update(@RequestHeader String authorization) {
        try{
            DecodedJWT decodedJWT = TokenUtility.verifyToken(authorization);
            String email = decodedJWT.getSubject();
            User oldUser = userService.fetchByEmail(email);
            UserAdminDTO deleteUser = new UserAdminDTO(false, true, oldUser.isConfirmed(), oldUser.getRoles());

            userService.update(oldUser.getId(), deleteUser);

            return ResponseEntity.noContent().build();
        }
        catch (IllegalArgumentException e){
            return ResponseEntity.notFound().build();
        }
        catch (IllegalAccessException e){
            return ResponseEntity.status(403).build();
        }
        catch (RuntimeException e){
            return ResponseEntity.status(400).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/role")
    public ResponseEntity<String> createRole(@RequestBody String roleName){
        try{
            long id = userService.createRole(roleName);
            final String location = String.format("/identity/%d", id);
            return ResponseEntity.created(URI.create(location)).body(String.valueOf(id));
        }
        catch (IllegalArgumentException e){
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    @GetMapping("/role")
    public ResponseEntity<List<Role>> fetchAllRoles(){
        return ResponseEntity.ok(userService.fetchAllRoles());
    }

    @GetMapping("/role/{id}")
    public ResponseEntity<Role> fetchRoleById(@PathVariable long id){
        try {
            return ResponseEntity.ok(userService.fetchRoleById(id));
        }
        catch (IllegalArgumentException e){
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/token/refresh")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try{
            String authorizationHeader = request.getHeader(AUTHORIZATION);
            DecodedJWT decodedJWT = TokenUtility.verifyToken(authorizationHeader);
            String email = decodedJWT.getSubject();
            User user = userService.fetchByEmail(email);
            TokenUtility tokenUtility = new TokenUtility(email, user.getId(), user.getRoles().stream().map(Role::getRoleName).collect(Collectors.toList()));
            Map<String, String> tokens = tokenUtility.generateAccessToken(request.getRequestURI());
            response.setContentType(APPLICATION_JSON_VALUE);
            new ObjectMapper().writeValue(response.getOutputStream(), tokens);
        }
        catch (Exception e){
            response.setStatus(FORBIDDEN.value());
            response.setHeader("error", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error_message", e.getMessage());
            response.setContentType(APPLICATION_JSON_VALUE);
            new ObjectMapper().writeValue(response.getOutputStream(), error);
        }
    }
}
