package ua.kpi.iasa.sc.newsservice.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ua.kpi.iasa.sc.newsservice.security.filter.CustomAuthorizationFilter;

import static org.springframework.http.HttpMethod.*;
import static org.springframework.http.HttpMethod.DELETE;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(super.userDetailsService()).passwordEncoder(bCryptPasswordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.authorizeRequests()
                .antMatchers(GET, "/news/**").permitAll()
                .antMatchers(POST, "/news/**").hasAnyAuthority("Admin", "Elderly", "Teacher")
                .antMatchers(PATCH, "/news/**").hasAnyAuthority("Admin", "Elderly", "Teacher")
                .antMatchers(DELETE, "/news/**").hasAnyAuthority("Admin")
                .anyRequest().permitAll();

        http.addFilterBefore(new CustomAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);
    }


    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception{
            return super.authenticationManagerBean();
    }
}