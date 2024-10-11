package com.dlj4.tech.queue.imp;

import com.dlj4.tech.queue.dao.request.UserDAO;
import com.dlj4.tech.queue.repository.UserRepository;
import com.dlj4.tech.queue.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImp  implements UserService {
    @Autowired
    private UserRepository userRepository;


    @Override
    public UserDetailsService userDetailService() {
        return username -> userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found!"));
    }
}
