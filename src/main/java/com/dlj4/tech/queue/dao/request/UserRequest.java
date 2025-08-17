package com.dlj4.tech.queue.dao.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserRequest {
    
    @NotBlank(message = "Username is required and cannot be empty")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9_.-]+$", message = "Username can only contain letters, numbers, dots, underscores, and hyphens")
    private String username;
    
    @NotBlank(message = "Password is required and cannot be empty")
    @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d).*$", message = "Password must contain at least one letter and one number")
    private String password;
    
    @NotBlank(message = "Email is required and cannot be empty")
    @Email(message = "Please provide a valid email address (e.g., user@example.com)")
    @Size(max = 100, message = "Email cannot exceed 100 characters")
    private String email;
    
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Please provide a valid phone number (e.g., +1234567890)")
    @Size(max = 20, message = "Phone number cannot exceed 20 characters")
    private String phone;
    
    @NotBlank(message = "Full name is required and cannot be empty")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    @Pattern(regexp = "^[a-zA-Z\\s'-]+$", message = "Name can only contain letters, spaces, apostrophes, and hyphens")
    private String name;
    
    @NotBlank(message = "Status is required (e.g., ACTIVE, INACTIVE)")
    @Pattern(regexp = "^(ACTIVE|INACTIVE)$", message = "Status must be either ACTIVE or INACTIVE")
    private String status;
    
    @Size(max = 255, message = "Address cannot exceed 255 characters")
    private String address;
    
    @Pattern(regexp = "^\\d+$", message = "Window ID must be a valid number")
    private String windowId;
    
    @NotBlank(message = "Role is required and cannot be empty")
    @Pattern(regexp = "^(ADMIN|USER)$", message = "Role must be either ADMIN or USER")
    private String role;
}
