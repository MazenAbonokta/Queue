package com.dlj4.tech.queue.controller;

import com.dlj4.tech.queue.service.TokenCleanupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

import java.util.HashMap;
import java.util.Map;

/**
 * Token Cleanup Administration Controller
 * 
 * Handles administrative operations for JWT token management and cleanup.
 * Provides endpoints for manual cleanup of expired tokens and system maintenance.
 * 
 * Base URL: /admin/tokens
 * 
 * @author Queue Management System
 * @version 1.0
 */
@Tag(name = "Token Administration", description = "Administrative operations for JWT token management and cleanup")
@RestController
@RequestMapping("/admin/tokens")
@RequiredArgsConstructor
@Slf4j
public class TokenCleanupController {
    
    private final TokenCleanupService tokenCleanupService;
    
    /**
     * Cleanup Expired Tokens (Admin Only)
     * 
     * Manually triggers cleanup of expired JWT tokens from the database.
     * Removes tokens that are past their expiration date to maintain database performance.
     * 
     * @return Map containing cleanup results and statistics
     * 
     * @apiNote POST /admin/tokens/cleanup/expired
     * @apiAuth Required: ADMIN role
     * @apiSuccess 200 {
     *   "success": true,
     *   "message": "Expired tokens cleaned up successfully",
     *   "cleanedCount": "number of tokens removed",
     *   "timestamp": "cleanup execution time"
     * }
     * @apiError 401 "Authentication required"
     * @apiError 403 "Admin role required"
     * @apiError 500 "Token cleanup failed"
     */
    @Operation(
            summary = "Cleanup Expired Tokens",
            description = "Manually trigger cleanup of expired JWT tokens from database for maintenance",
            security = {@SecurityRequirement(name = "JWT Authentication"), @SecurityRequirement(name = "Custom Token Authentication")}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Expired tokens cleaned successfully",
                    content = @Content(schema = @Schema(type = "object"),
                            examples = @ExampleObject(value = """
                                    {
                                      "success": true,
                                      "message": "Expired tokens cleaned up successfully",
                                      "cleanedCount": 15,
                                      "timestamp": "2024-01-15T10:30:00"
                                    }"""))),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Admin role required"),
            @ApiResponse(responseCode = "500", description = "Token cleanup failed")
    })
    @PostMapping("/cleanup/expired")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> cleanupExpiredTokens() {
        try {
            log.info("Manual cleanup of expired tokens initiated");
            int cleanedCount = tokenCleanupService.cleanupExpiredTokens();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Expired tokens cleaned up successfully");
            response.put("cleanedCount", cleanedCount);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error during manual expired token cleanup: ", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error cleaning up expired tokens");
            response.put("error", e.getMessage());
            
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    @Operation(
            summary = "Cleanup Inactive Tokens",
            description = "Clean up inactive tokens older than specified days for database optimization",
            security = {@SecurityRequirement(name = "JWT Authentication"), @SecurityRequirement(name = "Custom Token Authentication")}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inactive tokens cleaned successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Admin role required"),
            @ApiResponse(responseCode = "500", description = "Token cleanup failed")
    })
    @PostMapping("/cleanup/inactive")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> cleanupInactiveTokens(
            @Parameter(description = "Number of days old for inactive token cleanup", example = "7")
            @RequestParam(defaultValue = "7") int daysOld) {
        try {
            log.info("Manual cleanup of inactive tokens older than {} days initiated", daysOld);
            int cleanedCount = tokenCleanupService.cleanupInactiveTokens(daysOld);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", String.format("Inactive tokens older than %d days cleaned up successfully", daysOld));
            response.put("cleanedCount", cleanedCount);
            response.put("daysOld", daysOld);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error during manual inactive token cleanup: ", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error cleaning up inactive tokens");
            response.put("error", e.getMessage());
            
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    @Operation(
            summary = "Cleanup User Tokens",
            description = "Clean up all inactive tokens for a specific user - useful for user account management",
            security = {@SecurityRequirement(name = "JWT Authentication"), @SecurityRequirement(name = "Custom Token Authentication")}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User tokens cleaned successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Admin role required"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Token cleanup failed")
    })
    @PostMapping("/cleanup/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> cleanupUserInactiveTokens(
            @Parameter(description = "User ID for token cleanup", required = true, example = "1")
            @PathVariable Long userId) {
        try {
            log.info("Manual cleanup of inactive tokens for user {} initiated", userId);
            int cleanedCount = tokenCleanupService.cleanupUserInactiveTokens(userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", String.format("Inactive tokens for user %d cleaned up successfully", userId));
            response.put("cleanedCount", cleanedCount);
            response.put("userId", userId);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error during manual user token cleanup: ", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error cleaning up user tokens");
            response.put("error", e.getMessage());
            
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    @Operation(
            summary = "Cleanup All Tokens",
            description = "Comprehensive cleanup of all expired and inactive tokens for complete database maintenance",
            security = {@SecurityRequirement(name = "JWT Authentication"), @SecurityRequirement(name = "Custom Token Authentication")}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All tokens cleaned successfully",
                    content = @Content(schema = @Schema(type = "object"),
                            examples = @ExampleObject(value = """
                                    {
                                      "success": true,
                                      "message": "All token cleanup completed successfully",
                                      "expiredTokensCleaned": 15,
                                      "inactiveTokensCleaned": 8,
                                      "totalCleaned": 23
                                    }"""))),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Admin role required"),
            @ApiResponse(responseCode = "500", description = "Token cleanup failed")
    })
    @PostMapping("/cleanup/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> cleanupAllTokens() {
        try {
            log.info("Manual cleanup of all expired and inactive tokens initiated");
            
            int expiredCount = tokenCleanupService.cleanupExpiredTokens();
            int inactiveCount = tokenCleanupService.cleanupInactiveTokens(7);
            int totalCleaned = expiredCount + inactiveCount;
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "All token cleanup completed successfully");
            response.put("expiredTokensCleaned", expiredCount);
            response.put("inactiveTokensCleaned", inactiveCount);
            response.put("totalCleaned", totalCleaned);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error during manual all token cleanup: ", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error cleaning up tokens");
            response.put("error", e.getMessage());
            
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
