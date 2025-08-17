package com.dlj4.tech.queue.controller;

import com.dlj4.tech.queue.dao.request.ConfigRequest;
import com.dlj4.tech.queue.dao.response.ConfigResponse;
import com.dlj4.tech.queue.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;

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


/**
 * Configuration Management Controller
 * 
 * Manages system configuration including screen settings, logos, and display messages.
 * Handles file uploads for main screen backgrounds and logo images.
 * 
 * Base URL: /config
 * 
 * @author Queue Management System
 * @version 1.0
 */
@Tag(name = "System Configuration", description = "System configuration management including screen settings and file uploads")
@RestController
@RequestMapping("config")
public class ConfigController {
    @Autowired
    ConfigService configService;
    /**
     * Upload/Create Screen Configuration
     * 
     * Creates new configuration or updates existing one based on ID presence.
     * Handles screen layouts, logos, and display messages with file validation.
     * 
     * @param configRequest ConfigRequest with configuration details
     * @return ConfigResponse with configuration information
     * 
     * @apiNote POST /config/upload-screen-config
     * @apiBody {
     *   "id": "number (positive, optional - if provided, updates existing config)",
     *   "mainScreenName": "string (max 100 chars, alphanumeric/dots/underscores/hyphens)",
     *   "mainScreenFileExtension": "string (max 10 chars, format: '.jpg', '.png')",
     *   "mainScreenOriginalName": "string (max 255 chars, original filename)",
     *   "logoName": "string (max 100 chars, alphanumeric/dots/underscores/hyphens)",
     *   "logoFileExtension": "string (max 10 chars, format: '.jpg', '.png')",
     *   "logoOriginalName": "string (max 255 chars, original filename)",
     *   "mainScreenMessage": "string (max 500 chars, display message for main screen)",
     *   "ticketScreenMessage": "string (max 300 chars, display message for ticket screen)",
     *   "logoImg": "string (max 1000 chars, base64 image data)",
     *   "mainScreenImg": "string (max 1000 chars, base64 image data)"
     * }
     * @apiSuccess 200 ConfigResponse with configuration details
     * @apiError 400 Validation errors: "File extension must start with a dot and contain only letters and numbers",
     *                 "Main screen message cannot exceed 500 characters",
     *                 "Logo image data is too large"
     * @apiError 500 "Failed to save configuration"
     */
    @Operation(
            summary = "Upload Screen Configuration",
            description = "Create new or update existing screen configuration with file uploads and validation",
            security = {@SecurityRequirement(name = "JWT Authentication"), @SecurityRequirement(name = "Custom Token Authentication")}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Configuration saved successfully",
                    content = @Content(schema = @Schema(implementation = ConfigResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "id": 1,
                                      "mainScreenName": "welcome_screen",
                                      "logoName": "company_logo",
                                      "mainScreenMessage": "Welcome to our service center",
                                      "ticketScreenMessage": "Please wait for your number"
                                    }"""))),
            @ApiResponse(responseCode = "400", description = "Validation errors",
                    content = @Content(schema = @Schema(implementation = com.dlj4.tech.queue.dao.response.ErrorResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/upload-screen-config")
    public ResponseEntity<ConfigResponse> UploadScreenConfig(
            @Parameter(description = "Configuration details with file data", required = true)
            @Valid @RequestBody ConfigRequest configRequest) {
        if(configRequest.getId()==null || configRequest.getId().toString()=="")
        {
         return  ResponseEntity.ok(configService.createConfig(configRequest));
        }
        else{
            return  ResponseEntity.ok(configService.updateConfig(configRequest));
        }
    }
    
    /**
     * Update Screen Configuration
     * 
     * Updates existing configuration or creates new one based on ID presence.
     * Similar to upload endpoint but uses PUT method for RESTful design.
     * 
     * @param configRequest ConfigRequest with updated configuration details
     * @return ConfigResponse with updated configuration information
     * 
     * @apiNote PUT /config/update-screen-config
     * @apiBody Same as upload-screen-config
     * @apiSuccess 200 ConfigResponse with updated configuration details
     * @apiError 400 Validation errors with detailed field messages
     * @apiError 404 "Configuration not found" (if ID provided but doesn't exist)
     * @apiError 500 "Failed to update configuration"
     */
    @Operation(
            summary = "Update Screen Configuration",
            description = "Update existing screen configuration or create new one with validation",
            security = {@SecurityRequirement(name = "JWT Authentication"), @SecurityRequirement(name = "Custom Token Authentication")}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Configuration updated successfully",
                    content = @Content(schema = @Schema(implementation = ConfigResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation errors"),
            @ApiResponse(responseCode = "404", description = "Configuration not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/update-screen-config")
    public ResponseEntity<ConfigResponse> UpdateScreenConfig(
            @Parameter(description = "Updated configuration details", required = true)
            @Valid @RequestBody ConfigRequest configRequest) {
        if(configRequest.getId().toString()=="")
        {
            return  ResponseEntity.ok(configService.createConfig(configRequest));
        }
        else{
            return  ResponseEntity.ok(configService.updateConfig(configRequest));
        }
    }
    
    /**
     * Get Current Configuration
     * 
     * Retrieves the current system configuration including all screen settings and images.
     * 
     * @return ConfigResponse with current configuration details
     * 
     * @apiNote GET /config/get-config
     * @apiSuccess 200 ConfigResponse with current configuration including:
     *                  - Screen settings and messages
     *                  - Logo and main screen image data
     *                  - File information and extensions
     * @apiError 404 "No configuration found"
     * @apiError 500 "Failed to retrieve configuration"
     */
    @Operation(
            summary = "Get Current Configuration",
            description = "Retrieve the current system configuration including screen settings and images"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Configuration retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ConfigResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "id": 1,
                                      "mainScreenName": "welcome_screen",
                                      "mainScreenFileExtension": ".jpg",
                                      "logoName": "company_logo",
                                      "logoFileExtension": ".png",
                                      "mainScreenMessage": "Welcome to our service center",
                                      "ticketScreenMessage": "Please wait for your number"
                                    }"""))),
            @ApiResponse(responseCode = "404", description = "No configuration found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/get-config")
    public ResponseEntity<ConfigResponse> GetConfigBy() {
       return ResponseEntity.ok(configService.getConfig());
    }


}
