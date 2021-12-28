package ua.kpi.iasa.sc.identityservice.security.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import ua.kpi.iasa.sc.identityservice.repository.model.User;
import ua.kpi.iasa.sc.identityservice.security.utility.TokenUtility;
import ua.kpi.iasa.sc.identityservice.service.UserService;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static java.util.Arrays.stream;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@AllArgsConstructor
public class CustomAuthorizationFilter extends OncePerRequestFilter {
    UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String path = request.getServletPath();
        if(path.equals("/identity/signin") || path.equals("/identity/signup")
                || path.equals("/identity/token/refresh") || path.equals("/identity/byids/unauthorized")){
            filterChain.doFilter(request, response);
        }
        else{
            String authorizationHeader = request.getHeader(AUTHORIZATION);
            try{
                DecodedJWT decodedJWT = TokenUtility.verifyToken(authorizationHeader);
                String email = decodedJWT.getSubject();
                User user = userService.fetchByEmail(email);
                if(user.isDeleted() || user.isBlocked()){
                    throw new AccessDeniedException("User was blocked or deleted");
                }
                if(!user.isConfirmed()){
                    throw new AccessDeniedException("User wasn't confirmed. Connect to your elderly or admins");
                }
                String[] roles = decodedJWT.getClaim("roles").asArray(String.class);
                Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
                stream(roles).forEach(role -> {
                    authorities.add(new SimpleGrantedAuthority(role));
                });
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(email, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                filterChain.doFilter(request, response);
            }
            catch (Exception e){
                log.error("error logging in: {}", e.getMessage());
                response.setStatus(FORBIDDEN.value());
                response.setHeader("error", e.getMessage());
                Map<String, String> error = new HashMap<>();
                error.put("error_message", e.getMessage());
                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), error);
            }
        }
    }
}
