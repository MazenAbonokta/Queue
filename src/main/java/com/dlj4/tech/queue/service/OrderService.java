package com.dlj4.tech.queue.service;

import com.dlj4.tech.queue.dao.request.OrderDAO;
import com.dlj4.tech.queue.entity.Order;
import com.dlj4.tech.queue.enums.OrderStatus;

public interface OrderService {
    public void createOrder(OrderDAO orderDAO);
    public void  callNumber(Long Number);
    public Order  fetchNextOrder(OrderDAO orderDAO);
    public void updateOrderStatus(Order order, OrderStatus orderStatus);
    public  Order getOrderById(Long OrderId);

}

