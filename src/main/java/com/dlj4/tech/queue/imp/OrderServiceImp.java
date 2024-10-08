package com.dlj4.tech.queue.imp;

import com.dlj4.tech.queue.dto.OrderDTO;
import com.dlj4.tech.queue.entity.Order;
import com.dlj4.tech.queue.entity.Window;
import com.dlj4.tech.queue.mapper.ObjectsDataMapper;
import com.dlj4.tech.queue.repository.OrderRepository;
import com.dlj4.tech.queue.service.OrderService;
import com.dlj4.tech.queue.service.ServiceService;
import com.dlj4.tech.queue.service.WindowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
