package com.dlj4.tech.queue.constants;

import java.util.List;

public class SecurityConstants {
    public static final List<String> PERMITTED_URLS = List.of(
            "/auth/signin",
            "/ws/**",
            "/order/CreateOrder/**",
            "/order/getLastTickets/**",
            "/service/list/**",
            "/config/get-config/**",
            "/print/**",
            "/static/uploads/**",
            "/v1/api/get-token",
            "/swagger-ui.html",
            "/swagger-ui/*",
            "/v3/api-docs/**",
            "/swagger-resources/**",
            "/webjars/**"
    );
}