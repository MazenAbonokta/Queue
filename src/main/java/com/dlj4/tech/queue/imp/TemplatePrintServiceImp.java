package com.dlj4.tech.queue.imp;

import com.dlj4.tech.queue.service.TemplatePrintService;
import com.itextpdf.html2pdf.HtmlConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.print.*;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.PrintQuality;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
public class TemplatePrintServiceImp implements TemplatePrintService {

    @Autowired
     TemplateEngine templateEngine;
    @Override
    public void printTicket(String title, String ticketNumber, String serviceType, String estimatedWait, String counter, String footer) {
        // Set up Thymeleaf context with dynamic data
        String dateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        Context context = new Context();
        context.setVariable("TITLE", title);
        context.setVariable("TICKET_NUMBER", ticketNumber);
        context.setVariable("DATE", dateTime);
        context.setVariable("SERVICE_TYPE", serviceType);
        context.setVariable("ESTIMATED_WAIT", estimatedWait);
        context.setVariable("COUNTER", counter);
        context.setVariable("FOOTER", footer);

        // Process the template with Thymeleaf
        String htmlContent = templateEngine.process("ticket", context);

        // Optionally, you can strip HTML tags if you need plain text for printing
        String printableText = htmlContent.replaceAll("<[^>]+>", "");

        // Convert to InputStream for printing
        ByteArrayInputStream inputStream = new ByteArrayInputStream(printableText.getBytes(StandardCharsets.UTF_8));

        // Locate a print service that supports document flavor
        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(DocFlavor.INPUT_STREAM.AUTOSENSE, null);
        if (printServices.length == 0) {
            System.out.println("No printers found.");
            return;
        }

        try {
            PrintService printService = printServices[0]; // Select the first available printer
            PrintRequestAttributeSet attributes = new HashPrintRequestAttributeSet();
            attributes.add(MediaSizeName.ISO_A4);
            attributes.add(PrintQuality.HIGH);

            // Create print job and print
            DocPrintJob job = printService.createPrintJob();
            Doc doc = new SimpleDoc(inputStream, DocFlavor.INPUT_STREAM.AUTOSENSE, null);
            job.print(doc, attributes);

            System.out.println("Printing complete.");
        } catch (PrintException e) {
            e.printStackTrace();
        }

    }
    public void saveTicketAsPdf(String title, String ticketNumber, String serviceType, String estimatedWait, String counter, String footer, String filePath) {
        // Set the current date and time
        String dateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        Map<String, Object> params = new HashMap<>();
        // Set up Thymeleaf context with dynamic data
        Context context = new Context();
         params.put("TITLE", title);
         params.put("TICKET_NUMBER", ticketNumber);
         params.put("DATETIME", dateTime); // Current date and time
         params.put("SERVICE_TYPE", serviceType);
         params.put("ESTIMATED_WAIT", estimatedWait);
         params.put("COUNTER", counter);
         params.put("FOOTER", footer);
         context.setVariables(params);

        // Process the template with Thymeleaf
        String htmlContent = templateEngine.process("ticket", context);

        // Convert HTML to PDF and save to file
        try (FileOutputStream fileOutputStream = new FileOutputStream(filePath)) {
            HtmlConverter.convertToPdf(htmlContent, fileOutputStream);
            System.out.println("PDF saved successfully to " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
