package com.dlj4.tech.queue.dao.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceResponse {
    private Long id;
    private String  code;
    private int start;
    private  int end;
    private  Long categoryId;
    private  String categoryName;
    private String name;
    private String endTime;
    private Long pendingOrdersCount;
    private String status;
    private String type;
    private Long currentNumber;

}
