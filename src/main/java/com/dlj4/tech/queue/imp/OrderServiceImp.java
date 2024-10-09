package com.dlj4.tech.queue.imp;

import com.dlj4.tech.queue.dto.OrderDTO;
import com.dlj4.tech.queue.entity.Order;
import com.dlj4.tech.queue.entity.Window;
import com.dlj4.tech.queue.enums.OrderStatus;
import com.dlj4.tech.queue.exception.ResourceNotFoundException;
import com.dlj4.tech.queue.mapper.ObjectsDataMapper;
import com.dlj4.tech.queue.repository.OrderRepository;
import com.dlj4.tech.queue.service.OrderService;
import com.dlj4.tech.queue.service.ServiceService;
import com.dlj4.tech.queue.service.WindowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@Service
public class OrderServiceImp implements OrderService {
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    ServiceService serviceService;
    @Autowired
    WindowService windowService;
    @Autowired
    ObjectsDataMapper objectsDataMapper;
    @Override
    public void createOrder(OrderDTO orderDTO) {
        com.dlj4.tech.queue.entity.Service fetchedService = serviceService.getServiceById(orderDTO.getServiceId());
        Long currentMaxNumber = orderRepository.findMaxCurrentNumberByServiceId(orderDTO.getServiceId());
        Long newCurrenNumber= currentMaxNumber==0?fetchedService.getStart():(currentMaxNumber+1);
        Window window = windowService.getWindowByID(orderDTO.getWindowId());
        Order order= objectsDataMapper.createOrderEntity(window,fetchedService,newCurrenNumber);
        orderRepository.save(order);
    }

    @Override
    public void callNumber(Long Number) {
        /*call number*/
    }

    @Override
    public Order fetchNextOrder(OrderDTO orderDTO) {

        Order CurrenOrder=getOrderById(orderDTO.getOrderId());
        updateOrderStatus(CurrenOrder,orderDTO.getOrderStatus());
        Order nextOrder= orderRepository.
                findOrderByOrderStatusAndService_IdOrderByIdDesc(orderDTO.getOrderStatus(),orderDTO.getServiceId());
        Window window = windowService.getWindowByID(orderDTO.getWindowId());
        nextOrder.setOrderStatus(OrderStatus.BOOKED);
        nextOrder.setCallDate(ZonedDateTime.now(ZoneId.of("UTC")));
        nextOrder.setWindow(window);
        orderRepository.save(nextOrder);
        return  nextOrder;

    }

    @Override
    public void updateOrderStatus(Order order, OrderStatus orderStatus) {


        order.setOrderStatus(orderStatus);
        orderRepository.save(order);
    }

    @Override
    public Order getOrderById(Long OrderId) {
        Order order= orderRepository.findById(OrderId)
                .orElseThrow(()->new ResourceNotFoundException("service ["+OrderId +"is not Exist"));
        return  order;
    }
}
