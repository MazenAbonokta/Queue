package com.dlj4.tech.queue.config;

import com.dlj4.tech.queue.constants.Role;
import com.dlj4.tech.queue.dao.request.UserRequest;
import com.dlj4.tech.queue.dao.request.WindowRequest;
import com.dlj4.tech.queue.dao.response.WindowResponse;
import com.dlj4.tech.queue.entity.User;
import com.dlj4.tech.queue.service.AuthenticationService;
import com.dlj4.tech.queue.service.OrderService;
import com.dlj4.tech.queue.service.WindowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;

@Configuration

public class DatabaseConfig {

    @Autowired
    AuthenticationService service;
    @Autowired
    WindowService windowService;
    @Autowired
    OrderService orderService;
   @Bean
    public CommandLineRunner init() {
        return args -> {
           orderService.updateOldTickets();
            User adminUser = service.findUserByUsername("admin");
            if(adminUser == null) {


                UserRequest user = new UserRequest("admin","admin","admin@gmail.com","00000","admin","Active","","", Role.ADMIN.toString());
                service.signUp(user);
            }

        };
    }
}