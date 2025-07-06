package com.dlj4.tech.queue.config;

import com.dlj4.tech.queue.service.*;
;
import org.springframework.boot.test.context.TestConfiguration;

import org.springframework.context.annotation.Bean;


import static org.mockito.Mockito.mock;

@TestConfiguration
public class QueueConfigurationTest {
    @Bean
    public CategoryService categoryService() {
        return mock(CategoryService.class);
    }
    @Bean
    public JwtService jwtService() {
        return mock(JwtService.class);
    }

    @Bean
    public UserService userService() {
        return mock(UserService.class);
    }

    @Bean
    public AuthenticationService authenticationService() {
        return mock(AuthenticationService.class);
    }

    @Bean
    public ConfigService configService() {
        return mock(ConfigService.class);
    }

    @Bean
    public OrderService orderService() {
        return mock(OrderService.class);
    }

    @Bean
    public TemplatePrintService printService() {
        return mock(TemplatePrintService.class);
    }

    @Bean
    public ServiceService serviceService() {
        return mock(ServiceService.class);
    }

    @Bean
    public WindowService windowService() {
        return mock(WindowService.class);
    }

    @Bean
    public WindowRoleService windowRoleService() {
        return mock(WindowRoleService.class);
    }

    @Bean
    public UserActionService userActionService() {
        return mock(UserActionService.class);
    }

}
