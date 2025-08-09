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

import java.util.List;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {
    
    private final AuthenticationService authenticationService;
    private final UserService userService;
    @GetMapping("/hello")
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok("Authentication service is running");
    }

    @PostMapping("/signin")
    public ResponseEntity<?> signIn(@Valid @RequestBody SigningRequest request) {
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

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody RefreshRequest request) {
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

    @PostMapping("/users/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addUser(@Valid @RequestBody UserRequest request) {
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

    @PutMapping("/users/update/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateUser(@PathVariable("id") Long id, 
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

    @DeleteMapping("/users/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable("id") Long id) {
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
