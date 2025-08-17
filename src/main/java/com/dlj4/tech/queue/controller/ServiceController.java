package com.dlj4.tech.queue.controller;

import com.dlj4.tech.queue.constants.ServiceStatus;
import com.dlj4.tech.queue.constants.ServiceType;
import com.dlj4.tech.queue.dao.request.ServiceRequest;
import com.dlj4.tech.queue.dao.response.ServiceResponse;
import com.dlj4.tech.queue.service.ServiceService;
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
 * Service Management Controller
 * 
 * Handles CRUD operations for service entities with comprehensive validation.
 * Services represent different types of operations available at windows.
 * 
 * Base URL: /service
 * 
 * @author Queue Management System
 * @version 1.0
 */
@Tag(name = "Service Management", description = "CRUD operations for services with validation and category assignments")
@RestController
@RequestMapping("service")
public class ServiceController {

    @Autowired
    ServiceService serviceService;
    @Operation(
            summary = "Create New Service",
            description = "Create a new service with comprehensive validation including range and category validation",
            security = {@SecurityRequirement(name = "JWT Authentication"), @SecurityRequirement(name = "Custom Token Authentication")}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Service created successfully",
                    content = @Content(schema = @Schema(implementation = ServiceResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "id": 1,
                                      "code": "PASS",
                                      "name": "Passport Services",
                                      "start": 1,
                                      "end": 100,
                                      "categoryId": 1,
                                      "endTime": "17:30:00",
                                      "serviceStatus": "ACTIVE",
                                      "serviceType": "GOVERNMENT"
                                    }"""))),
            @ApiResponse(responseCode = "400", description = "Validation errors",
                    content = @Content(schema = @Schema(implementation = com.dlj4.tech.queue.dao.response.ErrorResponseDto.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "status": 400,
                                      "error": "Validation Failed",
                                      "details": {
                                        "code": "Service code can only contain uppercase letters and numbers (e.g., PASS, ID01)",
                                        "end": "End number must be greater than start number"
                                      }
                                    }"""))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/create")
    public ResponseEntity<ServiceResponse> createService(
            @Parameter(description = "Service details with validation", required = true)
            @Valid @RequestBody ServiceRequest request){
        ServiceResponse serviceResponse=serviceService.createService(request);
        return new ResponseEntity<ServiceResponse>(serviceResponse , HttpStatus.CREATED);
    }
    
    /**
     * Update Existing Service
     * 
     * Updates service information with validation. Service ID is required in request body.
     * 
     * @param request ServiceRequest with updated service details (must include ID)
     * @return ServiceResponse with updated service information
     * 
     * @apiNote PUT /service/update/{id}
     * @apiBody Same as create service, but with required "id" field
     * @apiSuccess 201 ServiceResponse with updated service details
     * @apiError 400 Validation errors with detailed field messages
     * @apiError 404 "Service not found"
     * @apiError 500 "Failed to update service"
     */
    @Operation(
            summary = "Update Service",
            description = "Update existing service with comprehensive validation including range and category checks",
            security = {@SecurityRequirement(name = "JWT Authentication"), @SecurityRequirement(name = "Custom Token Authentication")}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Service updated successfully",
                    content = @Content(schema = @Schema(implementation = ServiceResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "id": 1,
                                      "code": "PASS",
                                      "name": "Passport Services Updated",
                                      "start": 1,
                                      "end": 150,
                                      "categoryId": 1,
                                      "endTime": "18:00:00",
                                      "serviceStatus": "ACTIVE",
                                      "serviceType": "GOVERNMENT"
                                    }"""))),
            @ApiResponse(responseCode = "400", description = "Validation errors",
                    content = @Content(schema = @Schema(implementation = com.dlj4.tech.queue.dao.response.ErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Service not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/update/{id}")
    public ResponseEntity<ServiceResponse>  updateService(
            @Parameter(description = "Service details with ID for update", required = true)
            @Valid @RequestBody ServiceRequest request){
        ServiceResponse serviceResponse =  serviceService.updateService(request.getId(),request);
        return new ResponseEntity<ServiceResponse>(serviceResponse , HttpStatus.CREATED);
    }

    /**
     * Delete Service
     * 
     * Soft deletes a service by ID. Service will be marked as deleted but not removed from database.
     * 
     * @param id Service ID to delete (must be positive number)
     * 
     * @apiNote DELETE /service/delete/{id}
     * @apiSuccess 200 Service successfully deleted
     * @apiError 400 "Invalid service ID"
     * @apiError 404 "Service not found"
     * @apiError 500 "Failed to delete service"
     */
    @Operation(
            summary = "Delete Service",
            description = "Soft delete a service by ID - service will be marked as deleted",
            security = {@SecurityRequirement(name = "JWT Authentication"), @SecurityRequirement(name = "Custom Token Authentication")}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Service deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid service ID"),
            @ApiResponse(responseCode = "404", description = "Service not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/delete/{id}")
    public  void deleteService(
            @Parameter(description = "Service ID to delete", required = true, example = "1")
            @PathVariable("id") Long id ){
        serviceService.deleteService(id);
    }

    /**
     * Get All Services
     * 
     * Retrieves list of all active services.
     * 
     * @return List of ServiceResponse objects
     * 
     * @apiNote GET /service/list
     * @apiSuccess 200 Array of ServiceResponse objects
     * @apiError 500 "Failed to retrieve services"
     */
    @Operation(
            summary = "Get All Services",
            description = "Retrieve list of all active services with their categories and configurations"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Services retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ServiceResponse.class),
                            examples = @ExampleObject(value = """
                                    [{
                                      "id": 1,
                                      "code": "PASS",
                                      "name": "Passport Services",
                                      "start": 1,
                                      "end": 100,
                                      "categoryName": "Government Services",
                                      "endTime": "17:30:00",
                                      "serviceStatus": "ACTIVE",
                                      "serviceType": "GOVERNMENT"
                                    }]"""))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/list")
    public ResponseEntity<List<ServiceResponse>> getServices(){
        return new ResponseEntity<List<ServiceResponse> >(serviceService.getServices(),HttpStatus.OK);
    }
    @Operation(
            summary = "Get Services by Status and Type",
            description = "Filter services by status (ACTIVE/INACTIVE) and type (GOVERNMENT/PRIVATE/HIDDEN)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Filtered services retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ServiceResponse.class),
                            examples = @ExampleObject(value = """
                                    [{
                                      "id": 1,
                                      "code": "PASS",
                                      "name": "Passport Services",
                                      "serviceStatus": "ACTIVE",
                                      "serviceType": "GOVERNMENT"
                                    }]"""))),
            @ApiResponse(responseCode = "400", description = "Invalid status or type values"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/getByStatusAndType")
    public ResponseEntity<List<ServiceResponse>> getByStatusAndType(
            @Parameter(description = "Service status filter", required = true, example = "ACTIVE")
            @RequestParam String status,
            @Parameter(description = "Service type filter", required = true, example = "GOVERNMENT") 
            @RequestParam String type){


        return new ResponseEntity<List<ServiceResponse> >(serviceService.getServicesByStatusAndType(ServiceStatus.valueOf(status), ServiceType.valueOf(type)),HttpStatus.OK);
    }
}
