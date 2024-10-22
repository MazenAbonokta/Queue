package com.dlj4.tech.queue.dao.request;

import com.dlj4.tech.queue.entity.Window;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WindowRoleDAO {
    private Window window;
    private List<Long> ServiceIds;
}
