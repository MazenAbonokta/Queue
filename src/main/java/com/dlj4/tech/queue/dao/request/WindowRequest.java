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

    private String ipAddress;
    
    @NotBlank(message = "Window number is required and cannot be empty")
    @Size(min = 1)
  //  @Pattern(regexp = "^[A-Z0-9]+$", message = "Window number can only contain uppercase letters and numbers (e.g., W01, WIN1)")
    private String windowNumber;
    

    private List< Long> services;
}
