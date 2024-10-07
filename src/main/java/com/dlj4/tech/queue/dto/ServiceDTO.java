package com.dlj4.tech.queue.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ServiceDTO {
    private String  code;
    private int start;
    private  int end;
    private  Long categoryId;
}
