package com.dlj4.tech.queue.dao.response;

import com.dlj4.tech.queue.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserOrders {
    ServiceResponse serviceResponse;

    OrderResponse currentOrder;
}
