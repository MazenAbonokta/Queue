package com.dlj4.tech.queue.dao.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ErrorResponseDto {
    
    private String message;
    private String errorCode;
    private LocalDateTime timestamp;
    private String path;
    
    public ErrorResponseDto(String message, String errorCode) {
        this.message = message;
        this.errorCode = errorCode;
        this.timestamp = LocalDateTime.now();
    }
}
