package com.dlj4.tech.queue.config;

import com.dlj4.tech.queue.dao.request.UserDAO;
import com.dlj4.tech.queue.entity.User;
import com.dlj4.tech.queue.enums.Role;
import com.dlj4.tech.queue.repository.UserRepository;
import com.dlj4.tech.queue.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration

public class DatabaseConfig {

    @Autowired
    AuthenticationService service;

    @Bean
    public CommandLineRunner init() {
        return args -> {
            UserDAO user = new UserDAO("admin","admin","admin@gmail.com","555","admin",Role.ADMIN);
            service.signUp(user);
        };
    }
}