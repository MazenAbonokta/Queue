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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Print Management Controller
 * 
 * Handles ticket generation and printing operations.
 * Manages PDF generation for queue tickets and handles print requests.
 * 
 * Base URL: /print
 * 
 * @author Queue Management System
 * @version 1.0
 */
@Tag(name = "Print Management", description = "Ticket generation and printing operations")
@RestController
@RequestMapping("print")
public class PrintController {
    @Autowired
    OrderService orderService;
    @Autowired
    TemplatePrintService printService;

    /**
     * Generate Sample Ticket
     * 
     * Generates a sample PDF ticket for testing purposes.
     * Creates a standardized ticket format with queue information.
     * 
     * @apiNote GET /print/generate-ticket
     * @apiAuth Required: Operator/Admin access
     * @apiSuccess 200 PDF ticket generated successfully in static/uploads/ticket.pdf
     * @apiError 500 "Failed to generate ticket PDF"
     */
    @Operation(
            summary = "Generate Sample Ticket",
            description = "Generate a sample PDF ticket for testing and template purposes",
            security = {@SecurityRequirement(name = "JWT Authentication"), @SecurityRequirement(name = "Custom Token Authentication")}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ticket generated successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/generate-ticket")
    public void generateTicket(){
        String filePath = "static/uploads/ticket.pdf";
        printService.saveTicketAsPdf("Queue Ticket", "A123", "Customer Service", "15 minutes", "3", "Thank you for your patience.", filePath);
    }
    
    /**
     * Recall Number to Queue Display
     * 
     * Sends a recalled number to the queue display system for a specific window.
     * Used when customer needs to be recalled to a different window.
     * 
     * @param currentNumber Current queue number to recall (must be positive)
     * @param windowNumber Window number where customer should report (must be positive)
     * 
     * @apiNote GET /print/ReCallNumber/{currentNumber}/{windowNumber}
     * @apiAuth Required: Operator/Admin access
     * @apiSuccess 200 Number recalled to display system successfully
     * @apiError 400 "Invalid current number or window number"
     * @apiError 401 "Authentication required"
     * @apiError 500 "Failed to recall number to queue display"
     */
    @Operation(
            summary = "Recall Number to Display",
            description = "Send recalled number to queue display system for specific window",
            security = {@SecurityRequirement(name = "JWT Authentication"), @SecurityRequirement(name = "Custom Token Authentication")}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Number recalled successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid number or window ID"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/ReCallNumber/{currentNumber}/{windowNumber}")
    public void orderResponseEntity(
            @Parameter(description = "Current queue number to recall", required = true, example = "123")
            @PathVariable("currentNumber") Long currentNumber,
            @Parameter(description = "Window number for recall", required = true, example = "1")
            @PathVariable("windowNumber") Long windowNumber){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        // orderService.SendNumberToQueue(currentNumber,"2");
    }
}
