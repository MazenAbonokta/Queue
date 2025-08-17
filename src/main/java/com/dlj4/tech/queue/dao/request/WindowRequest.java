package com.dlj4.tech.queue.dao.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WindowRequest {
    
    @NotBlank(message = "IP address is required and cannot be empty")
    @Pattern(regexp = "^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$", 
             message = "Please provide a valid IP address (e.g., 192.168.1.100)")
    private String ipAddress;
    
    @NotBlank(message = "Window number is required and cannot be empty")
    @Size(min = 2, max = 10, message = "Window number must be between 2 and 10 characters")
    @Pattern(regexp = "^[A-Z0-9]+$", message = "Window number can only contain uppercase letters and numbers (e.g., W01, WIN1)")
    private String windowNumber;
    
    @NotEmpty(message = "At least one service must be assigned to the window")
    @Size(min = 1, max = 20, message = "Window can have between 1 and 20 services")
    private List<@Positive(message = "Service ID must be a positive number") Long> services;
}
