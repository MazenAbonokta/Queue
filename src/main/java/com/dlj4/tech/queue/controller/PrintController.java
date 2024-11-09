package com.dlj4.tech.queue.controller;

import com.dlj4.tech.queue.entity.User;
import com.dlj4.tech.queue.service.OrderService;
import com.dlj4.tech.queue.service.TemplatePrintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.context.SecurityContextHolder;

@RestController
@RequestMapping("print")
public class PrintController {
    @Autowired
    OrderService orderService;
    @Autowired
    TemplatePrintService printService;

    @GetMapping("/generate-ticket")
    public void generateTicket(){
        String filePath = "static/uploads/ticket.pdf";
        printService.saveTicketAsPdf("Queue Ticket", "A123", "Customer Service", "15 minutes", "3", "Thank you for your patience.", filePath);
    }
    @GetMapping("/ReCallNumber/{currentNumber}/{windowNumber}")
    public void orderResponseEntity(@PathVariable("currentNumber") Long currentNumber, @PathVariable("windowNumber") Long windowNumber){
        User user =  (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

     //   orderService.SendNumberToQueue(currentNumber,"2");
    }
}
