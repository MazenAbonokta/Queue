package com.dlj4.tech.queue.dao.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderActionResponse {
    private Long id;
    private ZonedDateTime createdAt;
    private String orderStatus;
    private Long orderId;
    private String orderCode;
    private String serviceName;
    private String windowNumber;
}
