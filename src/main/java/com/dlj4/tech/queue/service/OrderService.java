package com.dlj4.tech.queue.service;

import com.dlj4.tech.queue.dao.request.OrderDAO;
import com.dlj4.tech.queue.dao.request.TransferRequestDTO;
import com.dlj4.tech.queue.dao.response.OrderResponse;
import com.dlj4.tech.queue.dao.response.TransferResponse;
import com.dlj4.tech.queue.dao.response.UserOrders;
import com.dlj4.tech.queue.dto.MainScreenTicket;
import com.dlj4.tech.queue.entity.Order;
import com.dlj4.tech.queue.constants.OrderStatus;
import com.dlj4.tech.queue.entity.ServiceEntity;
import com.dlj4.tech.queue.entity.User;
import com.dlj4.tech.queue.entity.Window;
import org.apache.coyote.BadRequestException;

import java.util.List;

public interface OrderService {
    public void createOrder(Long serviceId) throws BadRequestException;
    public void  callNumber(Long Number,String ScreenNumber,String Code,String IpAddress);
    public void  SendNumberToQueue(Long Number,String ScreenNumber,String Code,String IpAddress);
    public UserOrders  fetchNextOrder(OrderDAO orderDAO);
    public void updateOrderStatus(Order order, OrderStatus orderStatus);
    public  Order getOrderById(Long OrderId);
    public List<UserOrders> getOrdersByUserId(Long userId);
    public List<UserOrders> getOrdersByUserIdAndStatus(Long userId,OrderStatus orderStatus) ;
    public void reCallTicket(OrderDAO orderDAO);
    public  OrderResponse getLastCalledOrderByUserId(Long userId);
    public Long getCountByServiceIdAndStatus (Long serviceId,OrderStatus orderStatus);
    public List<MainScreenTicket> getLastTickets();
    public void   updateOldTickets();
    public void sendToArduino(String number, String ip);
    public TransferResponse createTransferRequest(TransferRequestDTO transferRequest);
    public  void transferOrder(Order order, ServiceEntity targetService, Window window, User user);

    public  boolean approveRequest(Long orderTransferId,Long userId);
    public  void rejectRequest(Long orderTransferId,Long userId);



}

