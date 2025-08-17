package com.dlj4.tech.queue.dao.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryRequest {
    
    @Positive(message = "Category ID must be a positive number")
    private Long id;
    
    @NotBlank(message = "Category name is required and cannot be empty")
    @Size(min = 2, max = 50, message = "Category name must be between 2 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9\\s&()-]+$", message = "Category name can only contain letters, numbers, spaces, and basic punctuation (e.g., Government Services)")
    private String name;
}
