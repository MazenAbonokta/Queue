package com.dlj4.tech.queue.service;

public interface TokenCleanupService {
    
    /**
     * Clean up expired tokens from the database
     * @return number of tokens cleaned up
     */
    int cleanupExpiredTokens();
    
    /**
     * Clean up inactive tokens older than specified days
     * @param daysOld number of days
     * @return number of tokens cleaned up
     */
    int cleanupInactiveTokens(int daysOld);
    
    /**
     * Clean up all inactive tokens for a specific user
     * @param userId the user ID
     * @return number of tokens cleaned up
     */
    int cleanupUserInactiveTokens(Long userId);
}
