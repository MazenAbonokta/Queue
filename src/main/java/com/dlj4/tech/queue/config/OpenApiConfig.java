package com.dlj4.tech.queue.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI/Swagger Configuration
 * 
 * Configures Swagger UI for interactive API documentation.
 * Provides comprehensive API documentation with authentication support.
 * 
 * Access Swagger UI at: http://localhost:8083/swagger-ui.html
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Queue Management System API")
                        .description("""
                                # Queue Management System API Documentation
                                
                                A comprehensive queue management system providing:
                                
                                ## 🎯 Core Features
                                - **User Authentication & Management** - JWT-based security
                                - **Service Management** - CRUD operations for services and categories
                                - **Window Management** - Service window assignments and operations
                                - **Order Management** - Queue order creation and processing
                                - **Real-time Dashboard** - Analytics and live queue monitoring
                                - **System Configuration** - File uploads and display settings
                                
                                ## 🔐 Authentication
                                Most endpoints require JWT authentication. Use the `/auth/signin` endpoint to obtain a token,
                                then include it in the `Authorization` header as `Bearer {token}` or in the `x-access-token` header.
                                
                                ## 🚀 Getting Started
                                1. Sign in with admin credentials to get a JWT token
                                2. Use the token to authenticate API requests
                                3. Explore the dashboard endpoints for real-time data
                                4. Use validation endpoints to understand field requirements
                                
                                ## 📊 Sample Data
                                The system includes rich sample data for testing:
                                - 5 Categories (Government, Banking, Healthcare, Education, Municipal)
                                - 10 Services with various statuses and types
                                - 8 Windows with service assignments
                                - 10+ Users with different roles
                                - 18+ Orders with mixed statuses for realistic testing
                                
                                ## 🔧 Error Handling
                                All endpoints return structured error responses with:
                                - Detailed field validation messages
                                - HTTP status codes
                                - Clear error descriptions
                                - Suggestions for fixing issues
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Queue Management System Team")
                                .email("support@queuemanagement.com")
                                .url("https://github.com/queuemanagement/api"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8083")
                                .description("Development Server"),
                        new Server()
                                .url("https://api.queuemanagement.com")
                                .description("Production Server")))
                .addSecurityItem(new SecurityRequirement()
                        .addList("JWT Authentication")
                        .addList("Custom Token Authentication"))
                .components(new Components()
                        .addSecuritySchemes("JWT Authentication", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Enter JWT token obtained from /auth/signin endpoint"))
                        .addSecuritySchemes("Custom Token Authentication", new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                                .name("x-access-token")
                                .description("Enter JWT token obtained from /auth/signin endpoint")));
    }
}

