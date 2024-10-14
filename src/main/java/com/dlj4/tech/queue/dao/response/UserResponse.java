package com.dlj4.tech.queue.dao.response;

import lombok.Builder;
import lombok.Data;

@Builder
@Data

public class UserResponse {
    Long id;
    String username;
    String name;
    String email;
    String phone;
    String address;
}
