package com.dlj4.tech.queue.mapper;

import com.dlj4.tech.queue.dto.OrderDTO;
import com.dlj4.tech.queue.dto.ServiceDTO;
import com.dlj4.tech.queue.dto.WindowDTO;
import com.dlj4.tech.queue.entity.*;
import com.dlj4.tech.queue.enums.OrderStatus;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Component
public class ObjectsDataMapper {

    public Service serviceDTOToServiceEntity(ServiceDTO serviceDTO, Category category){
        return Service.builder()
                .start(serviceDTO.getStart())
                .end(serviceDTO.getEnd())
                .category(category)
                .code(serviceDTO.getCode())

                .build();

    }
    public Window windowDTOToWindowEntity(WindowDTO windowDTO){
        return Window.builder()
                .windowNumber(windowDTO.getWindowNumber())
                .ipAddress(windowDTO.getIpAddress())
                .build();
    }
    public WindowRole createWindowEntity(Window window,Service service){
        return WindowRole.builder()
                .window(window)
                .service(service)
                .build();
    }
    public Order createOrderEntity(Window window,Service service,Long CurrentNumber){
        return Order.builder()
                .createdAt(ZonedDateTime.now(ZoneId.of("UTC")))
                .orderStatus(OrderStatus.PENDING)
                .currentNumber(CurrentNumber)
                .service(service)
                .window(window)
                .build();
    }
}
