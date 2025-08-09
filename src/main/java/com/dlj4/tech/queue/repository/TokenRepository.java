package com.dlj4.tech.queue.repository;

import com.dlj4.tech.queue.entity.Token;
import com.dlj4.tech.queue.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {
    
    Optional<Token> findByToken(String token);

    Optional<List<Token>> findAllByUserAndIsActive(User user, Boolean isActive);

    Optional<Token> findByRefreshToken(String refreshToken);
    
    // Token cleanup methods
    long countByExpiresAtBefore(Date date);
    
    @Modifying
    @Query("DELETE FROM Token t WHERE t.expiresAt < :date")
    void deleteByExpiresAtBefore(@Param("date") Date date);
    
    long countByIsActiveFalseAndExpiresAtBefore(Date date);
    
    @Modifying
    @Query("DELETE FROM Token t WHERE t.isActive = false AND t.expiresAt < :date")
    void deleteByIsActiveFalseAndExpiresAtBefore(@Param("date") Date date);
    
    @Query("SELECT COUNT(t) FROM Token t WHERE t.user.id = :userId AND t.isActive = false")
    long countByUserIdAndIsActiveFalse(@Param("userId") Long userId);
    
    @Modifying
    @Query("DELETE FROM Token t WHERE t.user.id = :userId AND t.isActive = false")
    void deleteByUserIdAndIsActiveFalse(@Param("userId") Long userId);
}