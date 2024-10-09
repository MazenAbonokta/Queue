package com.dlj4.tech.queue.service;

import com.dlj4.tech.queue.dto.OrderDTO;
import com.dlj4.tech.queue.entity.Order;
import com.dlj4.tech.queue.enums.OrderStatus;

public interface OrderService {
    public void createOrder(OrderDTO orderDTO);
    public void  callNumber(Long Number);
    public Order  fetchNextOrder(OrderDTO orderDTO);
    public void updateOrderStatus(Order order, OrderStatus orderStatus);
    public  Order getOrderById(Long OrderId);

}

