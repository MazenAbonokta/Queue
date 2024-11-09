package com.dlj4.tech.queue.config;

import com.dlj4.tech.queue.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static com.dlj4.tech.queue.constants.SecurityConstants.PERMITTED_URLS;
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
                        request.requestMatchers( PERMITTED_URLS.toArray(new String[0])).permitAll()
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
