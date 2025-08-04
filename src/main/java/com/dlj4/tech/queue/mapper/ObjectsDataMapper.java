package com.dlj4.tech.queue.mapper;

import com.dlj4.tech.queue.constants.ServiceStatus;
import com.dlj4.tech.queue.constants.TransferRequestStatus;
import com.dlj4.tech.queue.dao.request.*;
import com.dlj4.tech.queue.dao.response.*;
import com.dlj4.tech.queue.dto.MainScreenTicket;
import com.dlj4.tech.queue.entity.*;
import com.dlj4.tech.queue.constants.OrderStatus;
import com.dlj4.tech.queue.constants.Role;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalTime;
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
    String uploadDir = "src/main/resources/static/uploads/";
    public ServiceEntity serviceDTOToServiceEntity(ServiceRequest serviceRequest, Category category){
        return ServiceEntity.builder()
                .start(serviceRequest.getStart())
                .end(serviceRequest.getEnd())
                .category(category)
                .code(serviceRequest.getCode())
                .name(serviceRequest.getName())
                .Type(serviceRequest.getType())
                .status(serviceRequest.getStatus())
                .endTime(serviceRequest.getEndTime()==""?null:LocalTime.parse(serviceRequest.getEndTime()))
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
                .code(service.getCode())
                .service(service)
                .window(window)
                .today(true)
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
                .windowId(user.getWindow()==null?null:user.getWindow().getId().toString())
                .role(user.getRole().toString())
                .status(user.getStatus())
                .windowNumber(user.getWindow()==null?null:user.getWindow().getWindowNumber())
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
               .status(service.getStatus().toString())
               .type(service.getType().toString())
               .currentNumber(service.getOrders() ==null?0:service.getOrders().stream().filter(x->x.getOrderStatus() ==OrderStatus.PENDING).findFirst().orElse(null).getCurrentNumber())
               .endTime(service.getEndTime()==null?"":service.getEndTime().toString())
               .pendingOrdersCount(service.getOrders() ==null?0:service.getOrders().stream().filter(x->x.getOrderStatus() ==OrderStatus.PENDING).count())
               .build();

    }
    public ServiceEntity copyServiceRequestToServiceEntity(ServiceRequest serviceRequest,ServiceEntity serviceEntity,Category category){
        serviceEntity.setCategory(category);
        serviceEntity.setEnd(serviceRequest.getEnd());
        serviceEntity.setCode(serviceRequest.getCode());
        serviceEntity.setStart(serviceRequest.getStart());
        serviceEntity.setName(serviceRequest.getName());
        serviceEntity.setEndTime(LocalTime.parse(serviceRequest.getEndTime()));
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

    public  ConfigScreen configScreenRequestToConfigScreen(ConfigRequest configRequest ,String hashedName ,String hashedMainFilename)
    {
        return ConfigScreen.builder()
                .mainScreenFileExtension(configRequest.getLogoFileExtension())

                .mainScreenOriginalName(configRequest.getMainScreenOriginalName())
                .mainScreenName(hashedMainFilename)
                .logoName(hashedName)

                .logoFileExtension(configRequest.getLogoFileExtension())
                .logoOriginalName(configRequest.getLogoOriginalName())

                .build();
    }
    public ConfigResponse configScreenToConfigScreenResponse(ConfigScreen configScreen)  {
        byte[] logImageContent = new byte[0];
        byte[] mainScreenContect = new byte[0];
        try {
            Path filePath = Paths.get(uploadDir + configScreen.getLogoName());
            logImageContent = Files.readAllBytes(filePath);

            // You can dynamically set content type if needed

        } catch (IOException e) {

        }
        try {

            Path filePath = Paths.get(uploadDir + configScreen.getMainScreenName());
            mainScreenContect = Files.readAllBytes(filePath);
            // You can dynamically set content type if needed

        } catch (IOException e) {
System.out.println(e.getMessage());
        }
        return ConfigResponse.builder()
                .mainScreenFileExtension(configScreen.getMainScreenFileExtension())
                .mainScreenOriginalName(configScreen.getMainScreenOriginalName())
                .mainScreenName(configScreen.getMainScreenName())
                .logoName(configScreen.getLogoName())
                .logoFileExtension(configScreen.getLogoFileExtension())
                .logoOriginalName(configScreen.getLogoOriginalName())
                .id(configScreen.getId().toString())
                .logImg(logImageContent)
                .mainScreenImg(mainScreenContect)
                .ticketScreenMessage(configScreen.getTicketScreenMessage())
                .mainScreenMessage(configScreen.getMainScreenMessage())
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
                .serviceCode(order.getCode())
                .build();
    }

    public MainScreenTicket orderActionToMainScreenTicket(OrderAction orderAction)
    {
    return MainScreenTicket.builder()
            .ticketNumber(orderAction.getOrder().getCode() + '-' + orderAction.getOrder().getCurrentNumber())
            .counter(orderAction.getOrder().getWindow().getWindowNumber())
            .build();
    }

    public TransferResponse transferOrderToTransferResponseResponse(TransferRequest transferRequest)
    {
        return TransferResponse.builder()
                .requestId(transferRequest.getId())
                .order(transferRequest.getRequestService().getCode() + "-" + transferRequest.getOrder().getCurrentNumber())
                .requestedService(transferRequest.getRequestService().getName())
                .requestedWindow(transferRequest.getRequestWindow().getWindowNumber())
                .requestDate(transferRequest.getCreatedAt().format(DateTimeFormatter.ofPattern("YYYY-mm-dd HH:mm:ss")))
                .userRequester(transferRequest.getRequestUser().getName())
                .targetService(transferRequest.getResponseService().getName())
                .targetWindow(transferRequest.getResponseWindow().getWindowNumber())
                .responseDate(transferRequest.getUpdatedAt().format(DateTimeFormatter.ofPattern("YYYY-mm-dd HH:mm:ss")))
                .userResponse(transferRequest.getResponseUser()==null?"":transferRequest.getResponseUser().getName())
                .status(transferRequest.getRequestStatus())
                .build();
    }

    public TransferRequest transferRequestDtoToTransferRequest(User user, ServiceEntity requestedService,
                                                               Window window, ServiceEntity targetService)

    {
        return TransferRequest.builder()
                .requestUser(user)
                .requestService(requestedService)
                .responseService(targetService)
                .requestWindow(window)
                .createdAt(ZonedDateTime.now(ZoneId.of("UTC")))
                .requestStatus(TransferRequestStatus.SEND)
                .build();
    }
}
