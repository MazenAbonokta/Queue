package com.dlj4.tech.queue.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;


import static com.dlj4.tech.queue.constants.SecurityConstants.PERMITTED_URLS;


@TestConfiguration
@EnableWebSecurity
public class TestSecurityConfig   {

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity httpSecurity)   throws  Exception{
        httpSecurity.csrf(AbstractHttpConfigurer::disable)

                .authorizeHttpRequests(request ->
                        request.requestMatchers( PERMITTED_URLS.toArray(new String[0])).permitAll()
                                .anyRequest().permitAll()
                );


        return httpSecurity.build();
    }

}