package com.dlj4.tech.queue.config;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
public class CustomCorsConfiguration implements CorsConfigurationSource {
    
    @Value("${app.cors.allowed-origins:http://localhost:4200,http://127.0.0.1:4200}")
    private String allowedOrigins;
    
    @Value("${app.cors.allowed-methods:GET,POST,PUT,DELETE,OPTIONS}")
    private String allowedMethods;
    
    @Value("${app.cors.allowed-headers:Authorization,Content-Type,Accept,Origin,X-Requested-With,x-access-token}")
    private String allowedHeaders;
    
    @Value("${app.cors.exposed-headers:Authorization}")
    private String exposedHeaders;
    
    @Value("${app.cors.allow-credentials:true}")
    private boolean allowCredentials;
    
    @Value("${app.cors.max-age:3600}")
    private long maxAge;
    
    @Override
    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
        
        // Skip CORS for WebSocket requests
        if (request.getRequestURI().startsWith("/ws")) {
            log.debug("Skipping CORS configuration for WebSocket request: {}", request.getRequestURI());
            return null;
        }
        
        CorsConfiguration config = new CorsConfiguration();
        
        // Configure allowed origins
        List<String> origins = Arrays.asList(allowedOrigins.split(","));
        config.setAllowedOrigins(origins);
        
        // Configure allowed methods
        List<String> methods = Arrays.asList(allowedMethods.split(","));
        config.setAllowedMethods(methods);
        
        // Configure allowed headers
        List<String> headers = Arrays.asList(allowedHeaders.split(","));
        config.setAllowedHeaders(headers);
        
        // Configure exposed headers
        if (!exposedHeaders.isEmpty()) {
            List<String> exposed = Arrays.asList(exposedHeaders.split(","));
            config.setExposedHeaders(exposed);
        }
        
        // Configure credentials and caching
        config.setAllowCredentials(allowCredentials);
        config.setMaxAge(maxAge);
        
        log.debug("CORS configuration applied for request: {} with origins: {}", 
                request.getRequestURI(), origins);
                
        return config;
    }
}