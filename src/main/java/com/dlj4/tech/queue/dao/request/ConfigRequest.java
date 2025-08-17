package com.dlj4.tech.queue.dao.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ConfigRequest {
    
    @Positive(message = "Configuration ID must be a positive number")
    private Long id;
    
    @Size(max = 100, message = "Main screen name cannot exceed 100 characters")
    @Pattern(regexp = "^[a-zA-Z0-9._-]*$", message = "Main screen name can only contain letters, numbers, dots, underscores, and hyphens")
    private String mainScreenName;
    
    @Size(max = 10, message = "File extension cannot exceed 10 characters")
    @Pattern(regexp = "^\\.[a-zA-Z0-9]+$", message = "File extension must start with a dot and contain only letters and numbers (e.g., .jpg, .png)")
    private String mainScreenFileExtension;
    
    @Size(max = 255, message = "Original file name cannot exceed 255 characters")
    private String mainScreenOriginalName;
    
    @Size(max = 100, message = "Logo name cannot exceed 100 characters")
    @Pattern(regexp = "^[a-zA-Z0-9._-]*$", message = "Logo name can only contain letters, numbers, dots, underscores, and hyphens")
    private String logoName;
    
    @Size(max = 10, message = "Logo file extension cannot exceed 10 characters")
    @Pattern(regexp = "^\\.[a-zA-Z0-9]+$", message = "Logo file extension must start with a dot and contain only letters and numbers (e.g., .jpg, .png)")
    private String logoFileExtension;
    
    @Size(max = 255, message = "Logo original name cannot exceed 255 characters")
    private String logoOriginalName;
    
    @Size(max = 500, message = "Main screen message cannot exceed 500 characters")
    private String mainScreenMessage;
    
    @Size(max = 300, message = "Ticket screen message cannot exceed 300 characters")
    private String ticketScreenMessage;
    
    @Size(max = 1000, message = "Logo image data is too large")
    private String logoImg;
    
    @Size(max = 1000, message = "Main screen image data is too large")
    private String mainScreenImg;
}
