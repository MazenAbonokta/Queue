package com.dlj4.tech.queue.imp;

import com.dlj4.tech.queue.repository.TokenRepository;
import com.dlj4.tech.queue.service.TokenCleanupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenCleanupServiceImpl implements TokenCleanupService {
    
    private final TokenRepository tokenRepository;
    
    @Override
    @Transactional
    public int cleanupExpiredTokens() {
        Date now = new Date();
        
        try {
            // Find and count expired tokens before deletion
            long expiredCount = tokenRepository.countByExpiresAtBefore(now);
            
            if (expiredCount > 0) {
                // Delete expired tokens
                tokenRepository.deleteByExpiresAtBefore(now);
                log.info("Cleaned up {} expired tokens", expiredCount);
                return (int) expiredCount;
            } else {
                log.debug("No expired tokens found for cleanup");
                return 0;
            }
            
        } catch (Exception e) {
            log.error("Error during expired token cleanup: ", e);
            return 0;
        }
    }
    
    @Override
    @Transactional
    public int cleanupInactiveTokens(int daysOld) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysOld);
        Date cutoffDateAsDate = Date.from(cutoffDate.atZone(ZoneId.systemDefault()).toInstant());
        
        try {
            // Count inactive tokens older than cutoff date
            long inactiveCount = tokenRepository.countByIsActiveFalseAndExpiresAtBefore(cutoffDateAsDate);
            
            if (inactiveCount > 0) {
                // Delete inactive tokens older than cutoff date
                tokenRepository.deleteByIsActiveFalseAndExpiresAtBefore(cutoffDateAsDate);
                log.info("Cleaned up {} inactive tokens older than {} days", inactiveCount, daysOld);
                return (int) inactiveCount;
            } else {
                log.debug("No inactive tokens older than {} days found for cleanup", daysOld);
                return 0;
            }
            
        } catch (Exception e) {
            log.error("Error during inactive token cleanup: ", e);
            return 0;
        }
    }
    
    @Override
    @Transactional
    public int cleanupUserInactiveTokens(Long userId) {
        try {
            // Count user's inactive tokens
            long userInactiveCount = tokenRepository.countByUserIdAndIsActiveFalse(userId);
            
            if (userInactiveCount > 0) {
                // Delete user's inactive tokens
                tokenRepository.deleteByUserIdAndIsActiveFalse(userId);
                log.info("Cleaned up {} inactive tokens for user ID: {}", userInactiveCount, userId);
                return (int) userInactiveCount;
            } else {
                log.debug("No inactive tokens found for user ID: {}", userId);
                return 0;
            }
            
        } catch (Exception e) {
            log.error("Error during user inactive token cleanup for user ID {}: ", userId, e);
            return 0;
        }
    }
    
    /**
     * Scheduled cleanup job that runs daily at 2 AM
     * Cleans up expired tokens and inactive tokens older than 7 days
     */
    @Scheduled(cron = "0 0 2 * * ?")
    @Transactional
    public void scheduledTokenCleanup() {
        log.info("Starting scheduled token cleanup...");
        
        try {
            int expiredCleaned = cleanupExpiredTokens();
            int inactiveCleaned = cleanupInactiveTokens(7); // Clean inactive tokens older than 7 days
            
            log.info("Scheduled token cleanup completed. Expired: {}, Inactive: {}", 
                    expiredCleaned, inactiveCleaned);
                    
        } catch (Exception e) {
            log.error("Error during scheduled token cleanup: ", e);
        }
    }
}
