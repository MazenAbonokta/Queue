package com.dlj4.tech.queue.dao.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserRequest {
    @NotEmpty
    private String username;
    @Size(min = 6, message = "Password must be at least 8 characters long")
    private String password;
    @Email(message = "Email should be valid")
    private String email;
    private String phone;
    private String name;
    private String status;
    private String address;
    private String windowId;
    private String role;
}
