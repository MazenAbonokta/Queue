package com.dlj4.tech.queue.dao.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransferRequestDTO {
    
    @NotNull(message = "User ID is required")
    @Positive(message = "User ID must be a positive number")
    private Long userId;
    
    @NotNull(message = "Current service ID is required")
    @Positive(message = "Current service ID must be a positive number")
    private Long serviceId;
    
    @NotNull(message = "Current window ID is required")
    @Positive(message = "Current window ID must be a positive number")
    private Long windowId;
    
    @NotNull(message = "Target service ID is required")
    @Positive(message = "Target service ID must be a positive number")
    private Long targetServiceId;
    
    @AssertTrue(message = "Target service must be different from current service")
    public boolean isDifferentService() {
        return targetServiceId != null && serviceId != null && !targetServiceId.equals(serviceId);
    }
}
