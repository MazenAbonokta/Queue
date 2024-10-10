package com.dlj4.tech.queue.dao.request;

import lombok.Data;

@Data
public class UserDAO {
    private String username;
    private String password;
    private String email;
    private String phone;
    private String name;
}
