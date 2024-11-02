package com.dlj4.tech.queue.controller;

import com.dlj4.tech.queue.dao.request.OrderDAO;
import com.dlj4.tech.queue.dao.response.OrderResponse;
import com.dlj4.tech.queue.dao.response.UserOrders;
import com.dlj4.tech.queue.entity.Order;
import com.dlj4.tech.queue.entity.User;
import com.dlj4.tech.queue.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    OrderService orderService;

    @PutMapping("/CallNextNumber")
    public ResponseEntity<OrderResponse> CallNextNumber(@RequestBody OrderDAO orderDAO){

        OrderResponse order = orderService.fetchNextOrder(orderDAO);

        return new ResponseEntity<OrderResponse>(order, HttpStatus.OK);
    }
    @PutMapping("/ReCallTicket")
    public void orderResponseEntity(@RequestBody OrderDAO orderDAO){
        orderService.reCallTicket(orderDAO);
    }
    @GetMapping("/CreateOrder/{serviceId}")
    public void CreateOrder(@PathVariable("serviceId") Long serviceId){

        orderService.createOrder(serviceId);
    }

    @GetMapping("/getOrdersByUser")
    public ResponseEntity<List<UserOrders>> getOrdersByUser(){
        User user =  (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();


        return new ResponseEntity<List<UserOrders>>(
             orderService.getOrdersByUserId(user.getId()), HttpStatus.OK);

    }
}
