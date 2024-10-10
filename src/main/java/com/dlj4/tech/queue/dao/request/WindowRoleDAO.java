package com.dlj4.tech.queue.dao.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WindowRoleDAO {
    private long WindowId;
    private List<Long> ServiceIds;
}
