package com.dlj4.tech.queue;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@SpringBootApplication
@EnableWebSecurity
public class QueueApplication {

    public static void main(String[] args) {
        SpringApplication.run(QueueApplication.class, args);
    }

}
