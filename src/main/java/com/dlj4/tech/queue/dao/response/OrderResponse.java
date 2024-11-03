package com.dlj4.tech.queue.dao.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse {
    Long orderId;
    Long serviceId;
    Long currentNumber;
    String callDate;
    Long windowNumber;
    String serviceCode;


}
