package com.dlj4.tech.queue.dao.request;

import com.dlj4.tech.queue.entity.Window;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
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
    
    @NotNull(message = "Window information is required")
    @Valid
    private Window window;
    
    @NotEmpty(message = "At least one service must be assigned to the window role")
    @Size(min = 1, max = 20, message = "Window role can have between 1 and 20 services")
    private List<Long> serviceIds;
}
