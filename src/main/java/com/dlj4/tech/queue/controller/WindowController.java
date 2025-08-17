package com.dlj4.tech.queue.controller;

import com.dlj4.tech.queue.dao.request.WindowRequest;
import com.dlj4.tech.queue.dao.response.WindowResponse;
import com.dlj4.tech.queue.service.WindowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

import java.util.List;

/**
 * Window Management Controller
 * 
 * Manages service windows where customers are served. Each window has an IP address,
 * window number, and can be assigned to handle specific services.
 * 
 * Base URL: /window
 * 
 * @author Queue Management System
 * @version 1.0
 */
@Tag(name = "Window Management", description = "CRUD operations for service windows with IP validation and service assignments")
@RestController
@RequestMapping("/window")
public class WindowController {

    @Autowired
    WindowService windowService;
    @Operation(
            summary = "Get All Windows",
            description = "Retrieve list of all active windows with their IP addresses and assigned services"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Windows retrieved successfully",
                    content = @Content(schema = @Schema(implementation = WindowResponse.class),
                            examples = @ExampleObject(value = """
                                    [{
                                      "id": 1,
                                      "ipAddress": "192.168.1.101",
                                      "windowNumber": "W01",
                                      "services": [
                                        {"id": 1, "name": "Passport Services"},
                                        {"id": 2, "name": "ID Services"}
                                      ]
                                    }]"""))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/list")
    public ResponseEntity<List<WindowResponse>> getWindowList() {
        return new ResponseEntity<List<WindowResponse>>(windowService.getWindowsList(), HttpStatus.OK);
    }
    
    /**
     * Create New Window
     * 
     * Creates a new service window with IP address, window number, and service assignments.
     * 
     * @param windowRequest WindowRequest with window details
     * @return WindowResponse with created window information
     * 
     * @apiNote POST /window/create
     * @apiBody {
     *   "ipAddress": "string (valid IPv4 format, e.g., '192.168.1.100')",
     *   "windowNumber": "string (2-10 chars, uppercase letters/numbers, e.g., 'W01', 'WIN1')",
     *   "services": "array of numbers (1-20 service IDs, all must be positive)"
     * }
     * @apiSuccess 201 WindowResponse with created window details
     * @apiError 400 Validation errors: "IP address is required and cannot be empty",
     *                 "Please provide a valid IP address (e.g., 192.168.1.100)",
     *                 "Window number is required and cannot be empty",
     *                 "Window number can only contain uppercase letters and numbers",
     *                 "At least one service must be assigned to the window",
     *                 "Service ID must be a positive number"
     * @apiError 500 "Failed to create window"
     */
    @Operation(
            summary = "Create New Window",
            description = "Create a new service window with IP validation and service assignments",
            security = {@SecurityRequirement(name = "JWT Authentication"), @SecurityRequirement(name = "Custom Token Authentication")}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Window created successfully",
                    content = @Content(schema = @Schema(implementation = WindowResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "id": 1,
                                      "ipAddress": "192.168.1.101",
                                      "windowNumber": "W01",
                                      "services": [
                                        {"id": 1, "name": "Passport Services"},
                                        {"id": 2, "name": "ID Services"}
                                      ]
                                    }"""))),
            @ApiResponse(responseCode = "400", description = "Validation errors",
                    content = @Content(schema = @Schema(implementation = com.dlj4.tech.queue.dao.response.ErrorResponseDto.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "status": 400,
                                      "error": "Validation Failed",
                                      "details": {
                                        "ipAddress": "Please provide a valid IP address (e.g., 192.168.1.100)",
                                        "services": "At least one service must be assigned to the window"
                                      }
                                    }"""))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/create")
    public ResponseEntity<WindowResponse> createWindow(
            @Parameter(description = "Window details with IP address and service assignments", required = true)
            @Valid @RequestBody WindowRequest windowRequest)
    {
        WindowResponse windowResponse= windowService.createWindow(windowRequest);
        return  new ResponseEntity<WindowResponse>(windowResponse,HttpStatus.CREATED);
    }

    /**
     * Update Existing Window
     * 
     * Updates window information including IP address, window number, and service assignments.
     * 
     * @param id Window ID to update (must be positive number)
     * @param windowRequest WindowRequest with updated window details
     * @return WindowResponse with updated window information
     * 
     * @apiNote PUT /window/edit/{id}
     * @apiBody Same as create window request
     * @apiSuccess 201 WindowResponse with updated window details
     * @apiError 400 Validation errors with detailed field messages
     * @apiError 404 "Window not found"
     * @apiError 500 "Failed to update window"
     */
    @Operation(
            summary = "Update Window",
            description = "Update existing window with IP address and service assignment changes",
            security = {@SecurityRequirement(name = "JWT Authentication"), @SecurityRequirement(name = "Custom Token Authentication")}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Window updated successfully",
                    content = @Content(schema = @Schema(implementation = WindowResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "id": 1,
                                      "ipAddress": "192.168.1.102",
                                      "windowNumber": "W01-UPDATED",
                                      "services": [
                                        {"id": 1, "name": "Passport Services"},
                                        {"id": 3, "name": "License Services"}
                                      ]
                                    }"""))),
            @ApiResponse(responseCode = "400", description = "Validation errors"),
            @ApiResponse(responseCode = "404", description = "Window not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/edit/{id}")
    public ResponseEntity<WindowResponse>  updateWindow(
            @Parameter(description = "Window ID to update", required = true, example = "1")
            @PathVariable("id") Long id,
            @Parameter(description = "Updated window details", required = true)
            @Valid @RequestBody WindowRequest windowRequest)
    {
        WindowResponse windowResponse=   windowService.updateWindow(id,windowRequest);
        return  new ResponseEntity<WindowResponse>(windowResponse,HttpStatus.CREATED);
    }
    
    /**
     * Delete Window
     * 
     * Soft deletes a window by ID. Window will be marked as deleted but not removed from database.
     * 
     * @param id Window ID to delete (must be positive number)
     * 
     * @apiNote DELETE /window/delete/{id}
     * @apiSuccess 200 Window successfully deleted
     * @apiError 400 "Invalid window ID"
     * @apiError 404 "Window not found"
     * @apiError 500 "Failed to delete window"
     */
    @Operation(
            summary = "Delete Window",
            description = "Soft delete a window by ID - window will be marked as deleted",
            security = {@SecurityRequirement(name = "JWT Authentication"), @SecurityRequirement(name = "Custom Token Authentication")}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Window deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid window ID"),
            @ApiResponse(responseCode = "404", description = "Window not found"),
            @ApiResponse(responseCode = "409", description = "Window cannot be deleted - has active orders"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/delete/{id}")
    public void deleteWindow(
            @Parameter(description = "Window ID to delete", required = true, example = "1")
            @PathVariable("id") Long id ){
        windowService.deleteWindow(id);
    }


}
