package com.dlj4.tech.queue.dao.request;

import com.dlj4.tech.queue.constants.ServiceStatus;
import com.dlj4.tech.queue.constants.ServiceType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ServiceRequest {
    
    @Positive(message = "Service ID must be a positive number")
    private Long id;
    
    @NotBlank(message = "Service code is required and cannot be empty")
    @Size(min = 2, max = 10, message = "Service code must be between 2 and 10 characters")
    @Pattern(regexp = "^[A-Z0-9]+$", message = "Service code can only contain uppercase letters and numbers (e.g., PASS, ID01)")
    private String code;
    
    @Min(value = 1, message = "Start number must be at least 1")
    @Max(value = 9999, message = "Start number cannot exceed 9999")
    private int start;
    
    @Min(value = 1, message = "End number must be at least 1")
    @Max(value = 9999, message = "End number cannot exceed 9999")
    private int end;
    
    @NotBlank(message = "Service name is required and cannot be empty")
    @Size(min = 3, max = 100, message = "Service name must be between 3 and 100 characters")
    @Pattern(regexp = "^[a-zA-Z0-9\\s&()-]+$", message = "Service name can only contain letters, numbers, spaces, and basic punctuation")
    private String name;
    
    @NotNull(message = "Category ID is required")
    @Positive(message = "Category ID must be a positive number")
    private Long categoryId;
    
    @Pattern(regexp = "^([01]?[0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$", message = "End time must be in HH:MM:SS format (e.g., 17:30:00)")
    private String endTime;
    
    @Size(max = 100, message = "Icon path cannot exceed 100 characters")
    @Pattern(regexp = "^[a-zA-Z0-9._/-]*$", message = "Icon path can only contain letters, numbers, dots, underscores, slashes, and hyphens")
    private String icon;
    
    @NotNull(message = "Service status is required")
    private ServiceStatus serviceStatus;
    
    @NotNull(message = "Service type is required")
    private ServiceType serviceType;
    
    @AssertTrue(message = "End number must be greater than start number")
    public boolean isValidRange() {
        return end > start;
    }
}
