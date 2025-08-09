package com.dlj4.tech.queue.controller;

import com.dlj4.tech.queue.service.TokenCleanupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin/tokens")
@RequiredArgsConstructor
@Slf4j
public class TokenCleanupController {
    
    private final TokenCleanupService tokenCleanupService;
    
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
    
    @PostMapping("/cleanup/inactive")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> cleanupInactiveTokens(
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
    
    @PostMapping("/cleanup/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> cleanupUserInactiveTokens(@PathVariable Long userId) {
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
