package com.dlj4.tech.queue.controller;

import com.dlj4.tech.queue.dao.request.OrderDAO;
import com.dlj4.tech.queue.entity.Order;
import com.dlj4.tech.queue.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class OrderController {
    @Autowired
    OrderService orderService;

    @PutMapping("/CallNextNumber")
    public ResponseEntity<Order> CallNextNumber(@RequestBody OrderDAO orderDAO){
        Order order = orderService.fetchNextOrder(orderDAO);
        return new ResponseEntity<Order>(order, HttpStatus.OK);
    }
    @GetMapping("/ReCallNumber/{currentNumber}")
    public void orderResponseEntity(@PathVariable Long currentNumber){
        orderService.callNumber(currentNumber);

    }
    @PostMapping("/CreateOrder")
    public void CreateOrder(@RequestBody OrderDAO orderDAO){
        orderService.createOrder(orderDAO);

    }
}
