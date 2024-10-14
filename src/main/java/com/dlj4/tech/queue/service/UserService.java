package com.dlj4.tech.queue.service;

import com.dlj4.tech.queue.dao.response.UserResponse;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService {


    UserDetailsService userDetailService();

    List<UserResponse> getUserResponseList();
}
