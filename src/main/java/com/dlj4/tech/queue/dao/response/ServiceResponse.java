package com.dlj4.tech.queue.dao.response;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ServiceResponse {
    private String  code;
    private int start;
    private  int end;
    private  Long categoryId;
    private  String categoryName;
    private String name;
}
