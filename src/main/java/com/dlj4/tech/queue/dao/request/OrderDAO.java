package com.dlj4.tech.queue.dao.request;

import com.dlj4.tech.queue.constants.OrderStatus;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrderDAO {
    
    @NotNull(message = "Order ID is required")
    @Positive(message = "Order ID must be a positive number")
    private Long orderId;
    
    @NotNull(message = "Service ID is required")
    @Positive(message = "Service ID must be a positive number")
    private Long serviceId;
    
    @NotNull(message = "Order status is required")
    private OrderStatus orderStatus;
}
