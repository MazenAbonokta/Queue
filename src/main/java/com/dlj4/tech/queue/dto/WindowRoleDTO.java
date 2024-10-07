package com.dlj4.tech.queue.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WindowRoleDTO {
    private long WindowId;
    private List<Long> ServiceIds;
}
