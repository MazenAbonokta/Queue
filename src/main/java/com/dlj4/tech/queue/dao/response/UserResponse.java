package com.dlj4.tech.queue.dao.response;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Builder
@Data

public class UserResponse {
    Long id;
    private String username;

    private String password;
    private String email;
    private String phone;
    private String name;
    private String status;
    private String address;
    private String windowId;
    private String role;
}
