package com.dlj4.tech.queue.service;

import com.dlj4.tech.queue.dao.request.OrderDAO;
import com.dlj4.tech.queue.dao.response.OrderResponse;
import com.dlj4.tech.queue.dao.response.UserOrders;
import com.dlj4.tech.queue.entity.Order;
import com.dlj4.tech.queue.entity.User;
import com.dlj4.tech.queue.enums.OrderStatus;

import java.util.List;

public interface OrderService {
    public void createOrder(Long serviceId);
    public void  callNumber(Long Number,String ScreenNumber);
    public void  SendNumberToQueue(Long Number,String ScreenNumber);
    public OrderResponse  fetchNextOrder(OrderDAO orderDAO);
    public void updateOrderStatus(Order order, OrderStatus orderStatus);
    public  Order getOrderById(Long OrderId);
    public List<UserOrders> getOrdersByUserId(Long userId);
    public void reCallTicket(OrderDAO orderDAO);
    public  OrderResponse getLastCalledOrderByUserId(Long userId);


}

