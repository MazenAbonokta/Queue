package com.dlj4.tech.queue.config;

import com.dlj4.tech.queue.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration

public class DatabaseConfig {

    @Autowired
    AuthenticationService service;

 /*   @Bean
    public CommandLineRunner init() {
        return args -> {
            UserRequest user = new UserRequest("admin","admin","admin@gmail.com","555","admin",Role.ADMIN);
            service.signUp(user);
        };
    }*/
}