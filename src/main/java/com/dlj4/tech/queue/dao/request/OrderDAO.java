package com.dlj4.tech.queue.dao.request;

import com.dlj4.tech.queue.constants.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrderDAO {


        Long orderId;
    Long serviceId;
    OrderStatus orderStatus;
}
