package com.dlj4.tech.queue.dao.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WindowRoleResponse {
    private Long id;
    private Long windowId;
    private String windowNumber;
    private Long serviceId;
    private String serviceName;
    private String serviceCode;
    private String categoryName;
}
