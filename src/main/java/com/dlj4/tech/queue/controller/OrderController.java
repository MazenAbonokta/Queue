package com.dlj4.tech.queue.controller;

import com.dlj4.tech.queue.dao.request.OrderDAO;
import com.dlj4.tech.queue.entity.Order;
import com.dlj4.tech.queue.entity.User;
import com.dlj4.tech.queue.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    OrderService orderService;

    @PutMapping("/CallNextNumber")
    public ResponseEntity<Order> CallNextNumber(@RequestBody OrderDAO orderDAO){
        Order order = orderService.fetchNextOrder(orderDAO);
        return new ResponseEntity<Order>(order, HttpStatus.OK);
    }
    @GetMapping("/ReCallNumber/{currentNumber}/{windowNumber}")
    public void orderResponseEntity(@PathVariable("currentNumber") Long currentNumber,@PathVariable("windowNumber") Long windowNumber){
        orderService.SendNumberToQueue(currentNumber,windowNumber);
    }
    @GetMapping("/CreateOrder/{serviceId}")
    public void CreateOrder(@PathVariable("serviceId") Long serviceId){

        orderService.createOrder(serviceId);
    }

    @PostMapping("/getOrdersByUser")
    public void getOrdersByUser(){
        User user =  (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        orderService.getOrdersByUser(user);
    }
}
