package com.dlj4.tech.queue.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:4200", "http://127.0.0.1:4200") // specify origins
                .allowedOriginPatterns("*") // optional for origin patterns
                .allowCredentials(true)
                .allowedHeaders("Authorization", "Content-Type", "Accept", "Origin", "X-Requested-With", "x-access-token")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS");
    }
}