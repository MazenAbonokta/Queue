package com.dlj4.tech.queue.imp;

import com.dlj4.tech.queue.dto.MainScreenTicket;
import com.dlj4.tech.queue.dto.TicketsMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {
    private final SimpMessagingTemplate template;


    public void  updateServiceTicketsCount(TicketsMessage ticketsMessage) {
            template.convertAndSend("/app/updateServiceTicketsCount", ticketsMessage);
    }


    public void  sendNewTicketToMainScreen(MainScreenTicket ticket) {
        template.convertAndSend("/app/addNewTicket", ticket);
    }
}
