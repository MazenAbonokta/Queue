package com.dlj4.tech.queue.mapper;

import com.dlj4.tech.queue.dao.request.ServiceDAO;
import com.dlj4.tech.queue.dao.request.UserRequest;
import com.dlj4.tech.queue.dao.request.WindowDAO;
import com.dlj4.tech.queue.dao.response.UserResponse;
import com.dlj4.tech.queue.entity.*;
import com.dlj4.tech.queue.enums.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class ObjectsDataMapper {
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    public Service serviceDTOToServiceEntity(ServiceDAO serviceDAO, Category category){
        return Service.builder()
                .start(serviceDAO.getStart())
                .end(serviceDAO.getEnd())
                .category(category)
                .code(serviceDAO.getCode())

                .build();

    }
    public Window windowDTOToWindowEntity(WindowDAO windowDAO){
        return Window.builder()
                .windowNumber(windowDAO.getWindowNumber())
                .ipAddress(windowDAO.getIpAddress())
                .build();
    }
    public WindowRole createWindowEntity(Window window,Service service){
        return WindowRole.builder()
                .window(window)
                .service(service)
                .build();
    }
    public Order createOrderEntity(Window window,Service service,Long CurrentNumber){
        return Order.builder()

                .orderStatus(OrderStatus.PENDING)
                .currentNumber(CurrentNumber)
                .service(service)
                .window(window)
                .build();
    }

    public User userDTOToUser(UserRequest userRequest){
        return User.builder()

                .username(userRequest.getUsername())
                .name(userRequest.getName())
                .email(userRequest.getEmail())
                .phone(userRequest.getPhone())
                .password(bCryptPasswordEncoder.encode(userRequest.getPassword()) )
                .role(userRequest.getRole())
                .build();
    }
    public UserResponse userToUserResponse(User user){
        return UserResponse.builder()

                .username(user.getUsername())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .address(user.getAddress())
                .id(user.getId())
                .build();
    }
    public User copyUserRequestToUser(User user,UserRequest userRequest){
        user.setAddress(userRequest.getAddress());
        user.setName(userRequest.getName());
        user.setPhone(userRequest.getPhone());
        user.setEmail(userRequest.getEmail());
        user.setPassword(bCryptPasswordEncoder.encode(userRequest.getPassword()));
        user.setStatus(userRequest.getStatus());
        return user;
    }
}
