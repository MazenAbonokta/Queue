package com.dlj4.tech.queue.controller;

import com.dlj4.tech.queue.dao.request.RefreshRequest;
import com.dlj4.tech.queue.dao.request.SigningRequest;
import com.dlj4.tech.queue.dao.request.UserRequest;
import com.dlj4.tech.queue.dao.response.ErrorResponseDto;
import com.dlj4.tech.queue.dao.response.JwtAuthenticationResponse;
import com.dlj4.tech.queue.dao.response.UserResponse;
import com.dlj4.tech.queue.exception.AuthenticationFailedException;
import com.dlj4.tech.queue.exception.RefreshTokenNotFoundException;
import com.dlj4.tech.queue.service.AuthenticationService;
import com.dlj4.tech.queue.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
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
 * Authentication Controller
 * 
 * Handles user authentication, registration, and token management operations.
 * All endpoints return detailed error messages for frontend validation display.
 * 
 * Base URL: /auth
 * 
 * @author Queue Management System
 * @version 1.0
 */
@Tag(name = "Authentication", description = "User authentication and management operations")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {
    
    private final AuthenticationService authenticationService;
    private final UserService userService;
    @Operation(
            summary = "Health Check",
            description = "Check if authentication service is running properly"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Service is running",
                    content = @Content(schema = @Schema(type = "string", example = "Authentication service is running")))
    })
    @GetMapping("/hello")
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok("Authentication service is running");
    }

    @Operation(
            summary = "User Sign In",
            description = "Authenticate user credentials and obtain JWT tokens for API access"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Authentication successful",
                    content = @Content(schema = @Schema(implementation = JwtAuthenticationResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "token": "eyJhbGciOiJIUzM4NCJ9...",
                                      "refreshToken": "REFRESH_eyJhbGciOiJIUzI1NiJ9...",
                                      "expiresAt": "2024-01-15T11:30:00Z"
                                    }"""))),
            @ApiResponse(responseCode = "400", description = "Validation errors",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "timestamp": "2024-01-15T10:30:00",
                                      "status": 400,
                                      "error": "Validation Failed",
                                      "message": "Please check the following fields and try again",
                                      "details": {
                                        "username": "Username is required and cannot be empty",
                                        "password": "Password must be between 3 and 100 characters"
                                      }
                                    }"""))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PostMapping("/signin")
    public ResponseEntity<?> signIn(
            @Parameter(description = "User credentials for authentication", required = true)
            @Valid @RequestBody SigningRequest request) {
        try {
            // Input validation
            if (request == null || !StringUtils.hasText(request.getUsername()) || !StringUtils.hasText(request.getPassword())) {
                log.warn("Invalid sign-in request: missing required fields");
                return ResponseEntity.badRequest()
                        .body(new ErrorResponseDto("Username and password are required", "INVALID_REQUEST"));
            }
            
            JwtAuthenticationResponse response = authenticationService.signIn(request);
            log.info("User signed in successfully: {}", request.getUsername());
            
            return ResponseEntity.ok(response);
            
        } catch (AuthenticationFailedException e) {
            log.warn("Authentication failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponseDto(e.getMessage(), "AUTHENTICATION_FAILED"));
        } catch (Exception e) {
            log.error("Unexpected error during sign-in: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponseDto("Internal server error", "INTERNAL_ERROR"));
        }
    }

    @Operation(
            summary = "Refresh JWT Token",
            description = "Generate new JWT token using valid refresh token for continued API access"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token refreshed successfully",
                    content = @Content(schema = @Schema(implementation = JwtAuthenticationResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "token": "eyJhbGciOiJIUzM4NFNf...",
                                      "refreshToken": "REFRESH_eyJhbGciOiJIN1J9...",
                                      "expiresAt": "2024-01-15T12:30:00Z"
                                    }"""))),
            @ApiResponse(responseCode = "400", description = "Invalid refresh token format",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Refresh token expired or not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(
            @Parameter(description = "Valid refresh token for generating new JWT", required = true)
            @Valid @RequestBody RefreshRequest request) {
        try {
            if (request == null || !StringUtils.hasText(request.getRefreshToken())) {
                log.warn("Invalid refresh token request: missing refresh token");
                return ResponseEntity.badRequest()
                        .body(new ErrorResponseDto("Refresh token is required", "INVALID_REQUEST"));
            }
            
            JwtAuthenticationResponse response = authenticationService.refreshToken(request);
            log.info("Token refreshed successfully");
            
            return ResponseEntity.ok(response);
            
        } catch (RefreshTokenNotFoundException e) {
            log.warn("Refresh token not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponseDto(e.getMessage(), "TOKEN_NOT_FOUND"));
        } catch (Exception e) {
            log.error("Unexpected error during token refresh: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponseDto("Token refresh failed", "INTERNAL_ERROR"));
        }
    }

    @Operation(
            summary = "Get All Users (Admin Only)",
            description = "Retrieve list of all registered users with their details - requires ADMIN role",
            security = {@SecurityRequirement(name = "JWT Authentication"), @SecurityRequirement(name = "Custom Token Authentication")}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully",
                    content = @Content(schema = @Schema(implementation = UserResponse.class),
                            examples = @ExampleObject(value = """
                                    [{
                                      "id": 1,
                                      "username": "john_doe",
                                      "email": "john@example.com",
                                      "name": "John Doe",
                                      "status": "ACTIVE",
                                      "role": "USER",
                                      "windowId": "1"
                                    }]"""))),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Admin role required"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getUsers() {
        try {
            List<UserResponse> users = userService.getUserResponseList();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            log.error("Error retrieving users: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Add New User (Admin Only)
     * 
     * Creates a new user account with comprehensive validation.
     * 
     * @param request UserRequest with complete user information
     * @return UserResponse with created user details
     * 
     * @apiNote POST /auth/users/add
     * @apiAuth Required: ADMIN role
     * @apiBody {
     *   "username": "string (3-50 chars, alphanumeric with dots/underscores/hyphens)",
     *   "password": "string (6-100 chars, must contain letter and number)",
     *   "email": "string (valid email format, max 100 chars)",
     *   "phone": "string (valid phone number format, e.g., +1234567890)",
     *   "name": "string (2-100 chars, letters/spaces/apostrophes/hyphens only)",
     *   "status": "string (ACTIVE or INACTIVE)",
     *   "address": "string (max 255 chars, optional)",
     *   "windowId": "string (numeric, optional)",
     *   "role": "string (ADMIN or USER)"
     * }
     * @apiSuccess 201 UserResponse with created user details
     * @apiError 400 Validation errors with detailed field messages
     * @apiError 401 "Authentication required"
     * @apiError 403 "Admin role required"
     * @apiError 500 "Failed to create user"
     */
    @Operation(
            summary = "Add New User (Admin Only)",
            description = "Create a new user account with comprehensive validation - requires ADMIN role",
            security = {@SecurityRequirement(name = "JWT Authentication"), @SecurityRequirement(name = "Custom Token Authentication")}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully",
                    content = @Content(schema = @Schema(implementation = UserResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "id": 2,
                                      "username": "jane_smith",
                                      "email": "jane@example.com",
                                      "name": "Jane Smith",
                                      "status": "ACTIVE",
                                      "role": "USER",
                                      "windowId": null
                                    }"""))),
            @ApiResponse(responseCode = "400", description = "Validation errors",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "status": 400,
                                      "error": "Validation Failed",
                                      "details": {
                                        "username": "Username must be between 3 and 50 characters",
                                        "email": "Please provide a valid email address",
                                        "password": "Password must contain at least one letter and one number"
                                      }
                                    }"""))),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Admin role required"),
            @ApiResponse(responseCode = "409", description = "Username or email already exists"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/users/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addUser(
            @Parameter(description = "Complete user information with validation", required = true)
            @Valid @RequestBody UserRequest request) {
        try {
            if (request == null || !StringUtils.hasText(request.getUsername())) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponseDto("Username is required", "INVALID_REQUEST"));
            }
            
            UserResponse response = authenticationService.signUp(request);
            log.info("User created successfully: {}", request.getUsername());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            log.error("Error creating user: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponseDto("Failed to create user", "INTERNAL_ERROR"));
        }
    }

    @Operation(
            summary = "Update User (Admin Only)",
            description = "Update existing user information with validation - requires ADMIN role",
            security = {@SecurityRequirement(name = "JWT Authentication"), @SecurityRequirement(name = "Custom Token Authentication")}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully",
                    content = @Content(schema = @Schema(type = "string", example = "User has been updated successfully"))),
            @ApiResponse(responseCode = "400", description = "Validation errors or invalid user ID",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Admin role required"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/users/update/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateUser(
            @Parameter(description = "User ID to update", required = true, example = "1")
            @PathVariable("id") Long id,
            @Parameter(description = "Updated user information", required = true)
            @Valid @RequestBody UserRequest request) {
        try {
            if (id == null || id <= 0) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponseDto("Valid user ID is required", "INVALID_REQUEST"));
            }
            
            authenticationService.updateUser(request);
            log.info("User updated successfully: ID {}", id);
            
            return ResponseEntity.ok().body("User has been updated successfully");
            
        } catch (Exception e) {
            log.error("Error updating user: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponseDto("Failed to update user", "INTERNAL_ERROR"));
        }
    }

    @Operation(
            summary = "Delete User (Admin Only)",
            description = "Soft delete a user account by ID - requires ADMIN role",
            security = {@SecurityRequirement(name = "JWT Authentication"), @SecurityRequirement(name = "Custom Token Authentication")}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User deleted successfully",
                    content = @Content(schema = @Schema(type = "string", example = "User has been deleted successfully"))),
            @ApiResponse(responseCode = "400", description = "Invalid user ID",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "message": "Valid user ID is required",
                                      "errorCode": "INVALID_REQUEST"
                                    }"""))),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Admin role required"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/users/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(
            @Parameter(description = "User ID to delete", required = true, example = "1")
            @PathVariable("id") Long id) {
        try {
            if (id == null || id <= 0) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponseDto("Valid user ID is required", "INVALID_REQUEST"));
            }
            
            authenticationService.deleteUser(id);
            log.info("User deleted successfully: ID {}", id);
            
            return ResponseEntity.ok().body("User has been deleted successfully");
            
        } catch (Exception e) {
            log.error("Error deleting user: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponseDto("Failed to delete user", "INTERNAL_ERROR"));
        }
    }
}
