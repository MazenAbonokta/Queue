package com.dlj4.tech.queue.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderMessageDto implements Serializable {
    private static final long serialVersionUID = 1L;
    Long TicketNumber;String WindowNumber;
    String Code;
}
