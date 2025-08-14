package com.dlj4.tech.queue.dao.response;

import com.dlj4.tech.queue.constants.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderArchiveResponse {
    private Long id;
    private Long currentNumber;
    private ZonedDateTime updatedAt;
    private ZonedDateTime createdAt;
    private ZonedDateTime callDate;
    private OrderStatus orderStatus;
    private String windowNumber;
    private String serviceName;
    private String categoryName;
    private String username;
}
