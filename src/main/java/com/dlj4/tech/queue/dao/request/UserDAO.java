package com.dlj4.tech.queue.dao.request;

import com.dlj4.tech.queue.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserDAO {
    private String username;
    private String password;
    private String email;
    private String phone;
    private String name;
    private Role role;
}
