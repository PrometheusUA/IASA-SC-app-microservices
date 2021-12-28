package ua.kpi.iasa.sc.complaintsservice.security.filter;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import ua.kpi.iasa.sc.complaintsservice.security.utility.TokenUtility;

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
@NoArgsConstructor
public class CustomAuthorizationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String path = request.getServletPath();
        String method = request.getMethod().toUpperCase();
        if(path.equals("/complaints") && method.equals("POST")){
            filterChain.doFilter(request, response);
        }
        else{
            try{
                String authorizationHeader = request.getHeader(AUTHORIZATION);
                DecodedJWT decodedJWT = TokenUtility.verifyToken(authorizationHeader);
                String email = decodedJWT.getSubject();
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
