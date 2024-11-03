package com.dlj4.tech.queue.service;

public interface TemplatePrintService {

    public void printTicket(String title, String ticketNumber , String serviceType, String estimatedWait, String counter, String footer);
    public void saveTicketAsPdf(String title, String ticketNumber, String serviceType, String estimatedWait, String counter, String footer, String filePath);
}
