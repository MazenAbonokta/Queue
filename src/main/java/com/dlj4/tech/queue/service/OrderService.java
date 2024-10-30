package com.dlj4.tech.queue.service;

import com.dlj4.tech.queue.dao.request.OrderDAO;
import com.dlj4.tech.queue.dao.response.OrderResponse;
import com.dlj4.tech.queue.entity.Order;
import com.dlj4.tech.queue.entity.User;
import com.dlj4.tech.queue.enums.OrderStatus;

import java.util.List;

public interface OrderService {
    public void createOrder(Long serviceId);
    public void  callNumber(Long Number,Long ScreenNumber);
    public void  SendNumberToQueue(Long Number,Long ScreenNumber);
    public Order  fetchNextOrder(OrderDAO orderDAO);
    public void updateOrderStatus(Order order, OrderStatus orderStatus);
    public  Order getOrderById(Long OrderId);
    public List<OrderResponse> getOrdersByUser(User user);


}

