package com.dlj4.tech.queue.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "token")
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "token", columnDefinition = "TEXT")
    private String token;
    
    @Column(name = "refresh_token", columnDefinition = "TEXT")
    private String refreshToken;
    
    @Column(name = "expires_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date expiresAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    public Token(String jwt, String refreshToken, Date expiresAt, User user,Boolean isActive) {
        this.token = jwt;
        this.refreshToken = refreshToken;
        this.expiresAt = expiresAt;
        this.user = user;
        this.isActive = isActive;
    }
}