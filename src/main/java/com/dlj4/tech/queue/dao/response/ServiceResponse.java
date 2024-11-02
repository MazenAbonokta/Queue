package com.dlj4.tech.queue.dao.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Builder
@Data
public class ServiceResponse {
    private Long id;
    private String  code;
    private int start;
    private  int end;
    private  Long categoryId;
    private  String categoryName;
    private String name;
    private String serviceDate;
    private Long pendingOrdersCount;
}
