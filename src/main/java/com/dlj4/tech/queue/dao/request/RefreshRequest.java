package com.dlj4.tech.queue.dao.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshRequest {
    
    @NotBlank(message = "Refresh token is required and cannot be empty")
    @Pattern(regexp = "^REFRESH_.*", message = "Invalid refresh token format")
    @Size(min = 10, max = 500, message = "Refresh token must be between 10 and 500 characters")
    private String refreshToken;
}