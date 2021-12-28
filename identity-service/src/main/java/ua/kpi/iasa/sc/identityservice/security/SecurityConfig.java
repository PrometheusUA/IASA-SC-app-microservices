package ua.kpi.iasa.sc.identityservice.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ua.kpi.iasa.sc.identityservice.security.filter.CustomAuthentificationFilter;
import ua.kpi.iasa.sc.identityservice.security.filter.CustomAuthorizationFilter;
import ua.kpi.iasa.sc.identityservice.service.UserService;

import static org.springframework.http.HttpMethod.*;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final UserDetailsService userDetailsService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserService userService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        CustomAuthentificationFilter customAuthentificationFilter = new CustomAuthentificationFilter(authenticationManagerBean(), userService);
        customAuthentificationFilter.setFilterProcessesUrl("/identity/signin");
        customAuthentificationFilter.setUsernameParameter("email");
        http.csrf().disable();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.authorizeRequests().antMatchers(POST, "/identity/role").hasAnyAuthority("Admin")
                .antMatchers(DELETE, "/identity/**").hasAnyAuthority("Admin")
                .antMatchers(PATCH, "/identity").hasAnyAuthority("Student")
                .antMatchers(PATCH, "/identity/**").hasAnyAuthority("Admin")
                .antMatchers(POST, "/identity/signin", "/identity/signup", "/identity/byids/unauthorized").permitAll()
                .antMatchers(GET,"/identity/token/refresh").permitAll()
                .antMatchers("/identity/admins", "/identity/byids").hasAnyAuthority("Admin")
                .antMatchers("/identity/**").hasAnyAuthority("Student")
                .anyRequest().permitAll();

        http.addFilterBefore(new CustomAuthorizationFilter(userService), UsernamePasswordAuthenticationFilter.class);
        http.addFilter(customAuthentificationFilter);
    }


    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception{
            return super.authenticationManagerBean();
    }
}
