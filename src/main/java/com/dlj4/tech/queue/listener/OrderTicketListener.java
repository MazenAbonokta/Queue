package com.dlj4.tech.queue.listener;

import com.dlj4.tech.queue.dto.OrderMessageDto;
import com.dlj4.tech.queue.service.OrderService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class OrderTicketListener {
    @Autowired
    OrderService orderService;


    @RabbitListener(queues = "#{@environment.getProperty('queue.name')}",concurrency = "1")
    public void receiveMessage(OrderMessageDto orderMessageDto) {
        orderService.callNumber(orderMessageDto.getTicketNumber(),orderMessageDto.getWindowNumber(),orderMessageDto.getCode(),orderMessageDto.getIpAddress());
    }
}
