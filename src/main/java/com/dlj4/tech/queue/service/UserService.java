package com.dlj4.tech.queue.service;

import com.dlj4.tech.queue.dao.request.UserDAO;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService {


    UserDetailsService userDetailService();
}
