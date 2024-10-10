package com.dlj4.tech.queue.dao.request;

import com.dlj4.tech.queue.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrderDAO {

    Long WindowId;
    Long OrderId;
    Long ServiceId;
    OrderStatus orderStatus;
}
