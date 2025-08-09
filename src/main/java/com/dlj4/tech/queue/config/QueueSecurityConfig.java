package com.dlj4.tech.queue.config;

import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class QueueSecurityConfig {
    
    private final AuthenticationProvider authenticationProvider;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomCorsConfiguration customCorsConfiguration;
    
    @Bean
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                // Disable CSRF for stateless REST API
                .csrf(AbstractHttpConfigurer::disable)
                
                // Configure CORS
                .cors(cors -> cors.configurationSource(customCorsConfiguration))
                
                // Configure security headers
                .headers(headers -> headers
                        .frameOptions(frameOptions -> frameOptions.deny())
                        .contentTypeOptions(contentTypeOptions -> {})
                        .httpStrictTransportSecurity(hstsConfig -> hstsConfig
                                .maxAgeInSeconds(31536000)
                                .includeSubDomains(true)
                        )
                )
                
                // Configure authorization rules
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PERMITTED_URLS.toArray(new String[0])).permitAll()
                        .anyRequest().authenticated()
                )
                
                // Configure exception handling
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(HttpStatus.FORBIDDEN.value());
                            response.getWriter().write("Access Denied");
                        })
                )
                
                // Configure session management
                .sessionManagement(session -> session
                        .sessionCreationPolicy(STATELESS)
                        .maximumSessions(1)
                        .maxSessionsPreventsLogin(false)
                )
                
                // Add authentication provider and JWT filter
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                
                .build();
    }
}
