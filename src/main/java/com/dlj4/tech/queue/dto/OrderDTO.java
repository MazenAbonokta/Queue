package com.dlj4.tech.queue.dto;

import com.dlj4.tech.queue.enums.OrderStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrderDTO {

    Long WindowId;
    Long OrderId;
    Long ServiceId;
    OrderStatus orderStatus;
}
