package com.dlj4.tech.queue.dao.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ServiceRequest {
    private Long  id;
    private String  code;
    private int start;
    private  int end;
    private String name;
    private  Long categoryId;
    private  String endTime;
}
