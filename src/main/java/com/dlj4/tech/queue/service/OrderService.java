package com.dlj4.tech.queue.service;

import com.dlj4.tech.queue.dao.request.OrderDAO;
import com.dlj4.tech.queue.dao.response.OrderResponse;
import com.dlj4.tech.queue.dao.response.UserOrders;
import com.dlj4.tech.queue.dto.MainScreenTicket;
import com.dlj4.tech.queue.entity.Order;
import com.dlj4.tech.queue.constants.OrderStatus;

import java.util.List;

public interface OrderService {
    public void createOrder(Long serviceId);
    public void  callNumber(Long Number,String ScreenNumber,String Code);
    public void  SendNumberToQueue(Long Number,String ScreenNumber,String Code);
    public UserOrders  fetchNextOrder(OrderDAO orderDAO);
    public void updateOrderStatus(Order order, OrderStatus orderStatus);
    public  Order getOrderById(Long OrderId);
    public List<UserOrders> getOrdersByUserId(Long userId);
    public void reCallTicket(OrderDAO orderDAO);
    public  OrderResponse getLastCalledOrderByUserId(Long userId);
    public Long getCountByServiceIdAndStatus (Long serviceId,OrderStatus orderStatus);
    public List<MainScreenTicket> getLastTickets();
    public void   updateOldTickets();

}

