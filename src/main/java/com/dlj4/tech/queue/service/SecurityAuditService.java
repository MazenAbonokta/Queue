package com.dlj4.tech.queue.service;

import com.dlj4.tech.queue.entity.User;

public interface SecurityAuditService {
    
    void logSuccessfulAuthentication(String username, String ipAddress);
    
    void logFailedAuthentication(String username, String ipAddress, String reason);
    
    void logTokenRefresh(String username, String ipAddress);
    
    void logLogout(String username, String ipAddress);
    
    void logSecurityEvent(String eventType, String username, String details, String ipAddress);
    
    void logPasswordChange(String username, String ipAddress);
    
    void logAccountLockout(String username, String ipAddress);
    
    void logPrivilegedAction(String username, String action, String ipAddress);
}
