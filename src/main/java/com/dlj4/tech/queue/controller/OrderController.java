package com.dlj4.tech.queue.controller;

import com.dlj4.tech.queue.constants.OrderStatus;
import com.dlj4.tech.queue.constants.ServiceType;
import com.dlj4.tech.queue.dao.request.OrderDAO;
import com.dlj4.tech.queue.dao.request.TransferRequestDTO;
import com.dlj4.tech.queue.dao.response.OrderResponse;
import com.dlj4.tech.queue.dao.response.ResponseDto;
import com.dlj4.tech.queue.dao.response.TransferResponse;
import com.dlj4.tech.queue.dao.response.UserOrders;
import com.dlj4.tech.queue.dto.MainScreenTicket;
import com.dlj4.tech.queue.entity.Order;
import com.dlj4.tech.queue.entity.ServiceEntity;
import com.dlj4.tech.queue.entity.User;
import com.dlj4.tech.queue.service.OrderService;
import com.dlj4.tech.queue.service.ServiceService;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    OrderService orderService;
@Autowired
    ServiceService serviceService;
    @PutMapping("/CallNextNumber")
    public ResponseEntity<UserOrders> CallNextNumber(@RequestBody OrderDAO orderDAO){

        UserOrders order = orderService.fetchNextOrder(orderDAO);

        return new ResponseEntity<UserOrders>(order, HttpStatus.OK);
    }
    @PutMapping("/ReCallTicket")
    public void orderResponseEntity(@RequestBody OrderDAO orderDAO){
        orderService.reCallTicket(orderDAO);
    }
    @GetMapping("/CreateOrder/{serviceId}")
    public void CreateOrder(@PathVariable("serviceId") Long serviceId) throws BadRequestException {

        orderService.createOrder(serviceId);
    }

    @GetMapping("/getOrdersByUser")
    public ResponseEntity<List<UserOrders>> getOrdersByUser(){
        User user =  (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();


        return new ResponseEntity<List<UserOrders>>(
             orderService.getOrdersByUserId(user.getId()), HttpStatus.OK);

    }

    @GetMapping("/getOrdersByUserAndStatus/{status}")
    public ResponseEntity<List<UserOrders>> getCanceledOrdersByUser(@RequestParam String status){
        User user =  (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();


        return new ResponseEntity<List<UserOrders>>(
                orderService.getOrdersByUserIdAndStatus(user.getId(), OrderStatus.valueOf(status)), HttpStatus.OK);

    }
    @GetMapping("/getLastTickets")
    public ResponseEntity<List<MainScreenTicket>> getLastTickets(){



        return new ResponseEntity<List<MainScreenTicket>>( orderService.getLastTickets(), HttpStatus.OK);

    }


    @PutMapping("/createTransferRequest/{id}")
    public ResponseEntity<ResponseDto> createTransferRequest(@PathVariable("id") Long id, @RequestBody TransferRequestDTO transferRequest){
        String Message="";
        ServiceEntity service= serviceService.getServiceById(transferRequest.getTargetServiceId());
        if(service!=null && service.getServiceType()== ServiceType.HIDDEN){
            orderService.transferOrder(id,transferRequest);
            Message="Order has been transferred";
        }
        else {
            orderService.createTransferRequest(id,transferRequest);
            Message="Transfer request has been created.";
        }
        return new ResponseEntity<ResponseDto>( ResponseDto.builder()
                .time(LocalDateTime.now())
                .message(Message)
                .code(HttpStatus.OK)
                .apiPath("/order/createTransferRequest/"+id)
                .build(), HttpStatus.OK);

    }
}
