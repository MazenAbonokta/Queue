package com.dlj4.tech.queue.mapper;

import com.dlj4.tech.queue.dao.request.*;
import com.dlj4.tech.queue.dao.response.*;
import com.dlj4.tech.queue.entity.*;
import com.dlj4.tech.queue.enums.OrderStatus;
import com.dlj4.tech.queue.enums.Role;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
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
                .createdAt(ZonedDateTime.now(ZoneId.of("UTC")))
                .orderStatus(OrderStatus.PENDING)
                .currentNumber(CurrentNumber)
                .service(service)
                .window(window)
                .build();
    }

    public User userDTOToUser(UserRequest userRequest,Window window ){
        return User.builder()

                .username(userRequest.getUsername())
                .name(userRequest.getName())
                .email(userRequest.getEmail())
                .phone(userRequest.getPhone())
                .status(userRequest.getStatus())
                .address(userRequest.getAddress())
                .window(window)
                .password(bCryptPasswordEncoder.encode(userRequest.getPassword()) )
                .role(Role.valueOf(userRequest.getRole()))
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
                .windowId(user.getWindow().getId().toString())
                .role(user.getRole().toString())
                .status(user.getStatus())

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
               .pendingOrdersCount(service.getOrders().stream().map(x->x.getCallDate() ==null).count())
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

    public  ConfigScreen configScreenRequestToConfigScreen(ConfigRequest configRequest,String path,String hashedName)
    {
        return ConfigScreen.builder()
                .configType(configRequest.getConfigType())
                .path( path)
                .content(configRequest.getEditor())
                .fileExtension(configRequest.getFileExt())
                .originalName(configRequest.getName())
                .name(hashedName)
                .build();
    }
    public ConfigResponse configScreenToConfigScreenResponse(ConfigScreen configScreen)
    {
        return ConfigResponse.builder()
                .configType(configScreen.getConfigType())
                .img(configScreen.getName())
                .content(configScreen.getContent())
                .fileExt(configScreen.getFileExtension())
                .fullPath(configScreen.getPath())
                .id(configScreen.getId().toString())

                .build();
    }

    public OrderResponse orderToOrderResponse(Order order)
    {
        return OrderResponse.builder()
                .orderId(order.getId())
                .serviceId(order.getService().getId())
                .currentNumber(order.getCurrentNumber())
                .callDate(order.getCallDate()==null?"": order.getCallDate() .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .windowNumber(order.getWindow()==null?0:order.getWindow().getId())
                .serviceCode(order.getWindow()==null?"-":order.getService().getCode())
                .build();
    }
}
