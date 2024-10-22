package com.dlj4.tech.queue.mapper;

import com.dlj4.tech.queue.dao.request.ServiceRequest;
import com.dlj4.tech.queue.dao.request.UserRequest;
import com.dlj4.tech.queue.dao.request.WindowRequest;
import com.dlj4.tech.queue.dao.request.WindowRoleDAO;
import com.dlj4.tech.queue.dao.response.CategoryResponse;
import com.dlj4.tech.queue.dao.response.ServiceResponse;
import com.dlj4.tech.queue.dao.response.UserResponse;
import com.dlj4.tech.queue.dao.response.WindowResponse;
import com.dlj4.tech.queue.entity.*;
import com.dlj4.tech.queue.enums.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ObjectsDataMapper {
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    public ServiceEntity serviceDTOToServiceEntity(ServiceRequest serviceRequest, Category category){
        return ServiceEntity.builder()
                .start(serviceRequest.getStart())
                .end(serviceRequest.getEnd())
                .category(category)
                .code(serviceRequest.getCode())
                .name(serviceRequest.getName())

                .build();

    }
    public Window windowDTOToWindowEntity(WindowRequest windowRequest){
        return Window.builder()
                .windowNumber(windowRequest.getWindowNumber())
                .ipAddress(windowRequest.getIpAddress())

                .build();
    }
    public WindowRole createWindowRoleEntity(Window window, ServiceEntity service){
        return WindowRole.builder()
                .window(window)
                .service(service)
                .build();
    }
    public Order createOrderEntity(Window window,ServiceEntity service,Long CurrentNumber){
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

    public ServiceResponse ServiceToServiceResponse(ServiceEntity service){
       return  ServiceResponse
               .builder()
               .categoryId(service.getCategory().getId())
               .categoryName(service.getCategory().getName())
               .code(service.getCode())
               .end(service.getEnd())
               .start(service.getStart())
               .name(service.getName())
               .id(service.getId())
               .build();

    }
    public ServiceEntity copyServiceRequestToServiceEntity(ServiceRequest serviceRequest,ServiceEntity serviceEntity,Category category){
        serviceEntity.setCategory(category);
        serviceEntity.setEnd(serviceRequest.getEnd());
        serviceEntity.setCode(serviceRequest.getCode());
        serviceEntity.setStart(serviceRequest.getStart());
        serviceEntity.setName(serviceRequest.getName());

        return serviceEntity;
    }

    public WindowResponse windowToWindowResponse(Window window)
    {
        return WindowResponse.builder()
                .id(window.getId())
                .windowNumber(window.getWindowNumber())
                .ipAddress(window.getIpAddress())
                .services(window.getWindowRoles()==null?new ArrayList<>():
                        window.getWindowRoles().stream().map(
                                windowRole -> ServiceToServiceResponse(windowRole.getService())
                        ).collect(Collectors.toList()))
                .build();
    }
    public Window copyWindowRequestToWindow(WindowRequest request,Window window)
    {
        window.setWindowNumber(request.getWindowNumber());
        window.setIpAddress(request.getIpAddress());
        return  window;
    }

    public CategoryResponse categoryToCategoryResponse(Category category)
    {
        return CategoryResponse.builder()
                .name(category.getName())
                .id(category.getId())
                .build();
    }
    public WindowRoleDAO windowToWindowRoleDAO(Window window, List<Long> ServiceIds)
    {
        return WindowRoleDAO.builder()
                .window(window)
                .ServiceIds(ServiceIds)
                .build();
    }
}
