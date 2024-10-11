package com.dlj4.tech.queue.mapper;

import com.dlj4.tech.queue.dao.request.ServiceDAO;
import com.dlj4.tech.queue.dao.request.UserDAO;
import com.dlj4.tech.queue.dao.request.WindowDAO;
import com.dlj4.tech.queue.entity.*;
import com.dlj4.tech.queue.enums.OrderStatus;
import com.dlj4.tech.queue.enums.Role;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    public User userDTOToUser(UserDAO userDAO){
        return User.builder()

                .username(userDAO.getUsername())
                .name(userDAO.getName())
                .email(userDAO.getEmail())
                .phone(userDAO.getPhone())
                .password(bCryptPasswordEncoder.encode(userDAO.getPassword()) )
                .role(userDAO.getRole())
                .build();
    }
}
