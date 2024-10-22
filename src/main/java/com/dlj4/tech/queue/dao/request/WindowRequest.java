package com.dlj4.tech.queue.dao.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class WindowRequest {
    String ipAddress;
    String windowNumber;
    private List<Long> services;
}
