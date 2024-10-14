package com.dlj4.tech.queue.imp;

import com.dlj4.tech.queue.dao.response.UserResponse;
import com.dlj4.tech.queue.entity.User;
import com.dlj4.tech.queue.mapper.ObjectsDataMapper;
import com.dlj4.tech.queue.repository.UserRepository;
import com.dlj4.tech.queue.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImp  implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    ObjectsDataMapper objectsDataMapper;

    @Override
    public UserDetailsService userDetailService() {
        return username -> userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found!"));
    }

    @Override
    public List<UserResponse> getUserResponseList() {
        List<User> users = userRepository.findAll();
        List<UserResponse> userResponseList = new ArrayList<>();
        userResponseList.addAll(users.stream().map(objectsDataMapper::userToUserResponse).collect(Collectors.toList()));
        return userResponseList;
    }
}
