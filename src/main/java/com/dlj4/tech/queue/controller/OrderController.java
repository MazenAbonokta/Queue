package com.dlj4.tech.queue.controller;

import com.dlj4.tech.queue.constants.OrderStatus;
import com.dlj4.tech.queue.constants.ServiceType;
import com.dlj4.tech.queue.dao.request.OrderDAO;
import com.dlj4.tech.queue.dao.request.TransferRequestDTO;
import com.dlj4.tech.queue.dao.response.OrderResponse;
import com.dlj4.tech.queue.dao.response.ResponseDto;
import com.dlj4.tech.queue.dao.response.TransferResponse;
import com.dlj4.tech.queue.dao.response.UserOrders;
import com.dlj4.tech.queue.dto.MainScreenTicket;
import com.dlj4.tech.queue.entity.Order;
import com.dlj4.tech.queue.entity.ServiceEntity;
import com.dlj4.tech.queue.entity.User;
import com.dlj4.tech.queue.service.OrderService;
import com.dlj4.tech.queue.service.ServiceService;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Order Management Controller
 * 
 * Handles queue order operations including creating orders, calling next customers,
 * recalling tickets, and managing order transfers between windows and services.
 * 
 * Base URL: /order
 * 
 * @author Queue Management System
 * @version 1.0
 */
@Tag(name = "Order Management", description = "Queue order operations including customer calls, recalls, and order creation")
@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    OrderService orderService;
@Autowired
    ServiceService serviceService;
    @Operation(
            summary = "Call Next Customer",
            description = "Fetch the next order in queue for a specific service and assign it to a window operator",
            security = {@SecurityRequirement(name = "JWT Authentication"), @SecurityRequirement(name = "Custom Token Authentication")}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Next customer called successfully",
                    content = @Content(schema = @Schema(implementation = UserOrders.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "id": 1,
                                      "orderNumber": "A001",
                                      "customerName": "John Doe",
                                      "serviceName": "Passport Services",
                                      "estimatedWaitTime": "15 minutes",
                                      "status": "CALLED"
                                    }"""))),
            @ApiResponse(responseCode = "400", description = "Validation errors",
                    content = @Content(schema = @Schema(implementation = com.dlj4.tech.queue.dao.response.ErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "No pending orders found for this service"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/CallNextNumber")
    public ResponseEntity<UserOrders> CallNextNumber(
            @Parameter(description = "Order details for calling next customer", required = true)
            @Valid @RequestBody OrderDAO orderDAO){
        UserOrders order = orderService.fetchNextOrder(orderDAO);
        return new ResponseEntity<UserOrders>(order, HttpStatus.OK);
    }
    
    /**
     * Recall Previous Ticket
     * 
     * Recalls a previously called ticket, useful when customer missed their call.
     * Updates order status back to CALLED and notifies display systems.
     * 
     * @param orderDAO OrderDAO with order details to recall
     * 
     * @apiNote PUT /order/ReCallTicket
     * @apiAuth Required: Operator/Admin access
     * @apiBody {
     *   "orderId": "number (positive, required)",
     *   "serviceId": "number (positive, required)",
     *   "orderStatus": "enum (ORDER_STATUS values)"
     * }
     * @apiSuccess 200 Ticket recalled successfully
     * @apiError 400 Validation errors with detailed field messages
     * @apiError 404 "Order not found or cannot be recalled"
     * @apiError 500 "Failed to recall ticket"
     */
    @Operation(
            summary = "Recall Previous Ticket",
            description = "Recall a previously called ticket for customer who missed their call",
            security = {@SecurityRequirement(name = "JWT Authentication"), @SecurityRequirement(name = "Custom Token Authentication")}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ticket recalled successfully"),
            @ApiResponse(responseCode = "400", description = "Validation errors",
                    content = @Content(schema = @Schema(implementation = com.dlj4.tech.queue.dao.response.ErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Order not found or cannot be recalled"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/ReCallTicket")
    public void orderResponseEntity(
            @Parameter(description = "Order details for recalling ticket", required = true)
            @Valid @RequestBody OrderDAO orderDAO){
        orderService.reCallTicket(orderDAO);
    }
    
    /**
     * Create New Order (Customer Self-Service)
     * 
     * Creates a new queue order for a customer for the specified service.
     * Generates queue number and estimated wait time.
     * 
     * @param serviceId Service ID for which to create the order (must be positive)
     * @throws BadRequestException if service is inactive or invalid
     * 
     * @apiNote GET /order/CreateOrder/{serviceId}
     * @apiAuth Required: Customer/User access
     * @apiSuccess 200 Order created successfully with queue number
     * @apiError 400 "Invalid service ID" or "Service is currently inactive"
     * @apiError 401 "Authentication required"
     * @apiError 500 "Failed to create order"
     */
    @Operation(
            summary = "Create New Order",
            description = "Create a new queue order for customer self-service - generates queue number and wait time",
            security = {@SecurityRequirement(name = "JWT Authentication"), @SecurityRequirement(name = "Custom Token Authentication")}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order created successfully with queue number"),
            @ApiResponse(responseCode = "400", description = "Invalid service ID or service inactive"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "404", description = "Service not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/CreateOrder/{serviceId}")
    public void CreateOrder(
            @Parameter(description = "Service ID for creating order", required = true, example = "1")
            @PathVariable("serviceId") Long serviceId) throws BadRequestException {
        orderService.createOrder(serviceId);
    }

    @Operation(
            summary = "Get User's Orders",
            description = "Retrieve all orders for the authenticated user",
            security = {@SecurityRequirement(name = "JWT Authentication"), @SecurityRequirement(name = "Custom Token Authentication")}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User orders retrieved successfully",
                    content = @Content(schema = @Schema(implementation = UserOrders.class),
                            examples = @ExampleObject(value = """
                                    [{
                                      "id": 1,
                                      "orderNumber": "A001",
                                      "serviceName": "Passport Services",
                                      "status": "PENDING",
                                      "estimatedWaitTime": "15 minutes",
                                      "createdAt": "2024-01-15T10:30:00"
                                    }]"""))),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/getOrdersByUser")
    public ResponseEntity<List<UserOrders>> getOrdersByUser(){
        User user =  (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();


        return new ResponseEntity<List<UserOrders>>(
             orderService.getOrdersByUserId(user.getId()), HttpStatus.OK);

    }

    @Operation(
            summary = "Get User's Orders by Status",
            description = "Retrieve user's orders filtered by specific status (PENDING, CALLED, COMPLETED, etc.)",
            security = {@SecurityRequirement(name = "JWT Authentication"), @SecurityRequirement(name = "Custom Token Authentication")}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Filtered user orders retrieved successfully",
                    content = @Content(schema = @Schema(implementation = UserOrders.class))),
            @ApiResponse(responseCode = "400", description = "Invalid status value"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/getOrdersByUserAndStatus/{status}")
    public ResponseEntity<List<UserOrders>> getCanceledOrdersByUser(
            @Parameter(description = "Order status filter", required = true, example = "PENDING")
            @RequestParam String status){
        User user =  (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();


        return new ResponseEntity<List<UserOrders>>(
                orderService.getOrdersByUserIdAndStatus(user.getId(), OrderStatus.valueOf(status)), HttpStatus.OK);

    }
    @Operation(
            summary = "Get Last Called Tickets",
            description = "Retrieve recently called tickets for main display screen"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Last tickets retrieved successfully",
                    content = @Content(schema = @Schema(implementation = MainScreenTicket.class),
                            examples = @ExampleObject(value = """
                                    [{
                                      "ticketNumber": "A001",
                                      "windowNumber": "W01",
                                      "serviceName": "Passport Services",
                                      "status": "CALLED",
                                      "calledAt": "2024-01-15T10:45:00"
                                    }]"""))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/getLastTickets")
    public ResponseEntity<List<MainScreenTicket>> getLastTickets(){



        return new ResponseEntity<List<MainScreenTicket>>( orderService.getLastTickets(), HttpStatus.OK);

    }


    @Operation(
            summary = "Create Transfer Request",
            description = "Create a request to transfer order to different service or perform immediate transfer for hidden services",
            security = {@SecurityRequirement(name = "JWT Authentication"), @SecurityRequirement(name = "Custom Token Authentication")}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transfer request created or order transferred",
                    content = @Content(schema = @Schema(implementation = ResponseDto.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "message": "Transfer request has been created.",
                                      "code": "OK",
                                      "time": "2024-01-15T10:30:00",
                                      "apiPath": "/order/createTransferRequest/1"
                                    }"""))),
            @ApiResponse(responseCode = "400", description = "Validation errors"),
            @ApiResponse(responseCode = "404", description = "Order or target service not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/createTransferRequest/{id}")
    public ResponseEntity<ResponseDto> createTransferRequest(
            @Parameter(description = "Order ID to transfer", required = true, example = "1")
            @PathVariable("id") Long id,
            @Parameter(description = "Transfer request details", required = true)
            @RequestBody TransferRequestDTO transferRequest){
        String Message="";
        ServiceEntity service= serviceService.getServiceById(transferRequest.getTargetServiceId());
        if(service!=null && service.getServiceType()== ServiceType.HIDDEN){
            orderService.transferOrder(id,transferRequest);
            Message="Order has been transferred";
        }
        else {
            orderService.createTransferRequest(id,transferRequest);
            Message="Transfer request has been created.";
        }
        return new ResponseEntity<ResponseDto>( ResponseDto.builder()
                .time(LocalDateTime.now())
                .message(Message)
                .code(HttpStatus.OK)
                .apiPath("/order/createTransferRequest/"+id)
                .build(), HttpStatus.OK);

    }
}
