package com.dlj4.tech.queue.config;

import com.dlj4.tech.queue.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity

public class QueueSecurityConfig {
    @Autowired
    AuthenticationProvider authenticationProvider;
    @Autowired
    private  JwtAuthenticationFilter jwtAuthenticationFilter;
    @Autowired

    private  UserService userService;
    @Autowired CustomCorsConfiguration customCorsConfiguration;
    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity httpSecurity)   throws  Exception{
        httpSecurity.csrf(AbstractHttpConfigurer::disable)
                .cors(c -> c.configurationSource(customCorsConfiguration))
                .authorizeHttpRequests(request ->
                        request.requestMatchers(  "/auth/**",
                                        "/v1/api/get-token",
                                        "/swagger-ui.html",
                                        "/swagger-ui/*",
                                        "/v3/api-docs/**",
                                        "/swagger-resources/**",
                                        "/webjars/**").permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exception -> {
                    exception.authenticationEntryPoint(
                            new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)
                    );
                })
                .sessionManagement(manager -> manager.sessionCreationPolicy(STATELESS))

                .authenticationProvider(authenticationProvider).addFilterBefore(
                        jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }

}
