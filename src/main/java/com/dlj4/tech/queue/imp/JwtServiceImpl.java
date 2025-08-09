package com.dlj4.tech.queue.imp;

import com.dlj4.tech.queue.entity.Token;
import com.dlj4.tech.queue.entity.User;
import com.dlj4.tech.queue.exception.TokenNotFoundException;
import com.dlj4.tech.queue.repository.TokenRepository;
import com.dlj4.tech.queue.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtServiceImpl implements JwtService {
    
    private final TokenRepository tokenRepository;

    @Value("${token.signing.key}")
    private String jwtSigningKey;

    @Value("${token.refresh.signing.key}")
    private String jwtRefreshSigningKey;

    @Value("${token.access.token.expiration}")
    private long accessTokenExpiration;

    @Value("${token.refresh.token.expiration}")
    private long refreshTokenExpiration;

    @Override
    public String extractUserName(String token) {
        try {
            return extractClaim(token, Claims::getSubject);
        } catch (JwtException e) {
            log.error("Error extracting username from JWT token: ", e);
            return null;
        }
    }

    @Override
    public String generateToken(UserDetails userDetails) {
        if (userDetails == null || userDetails.getUsername() == null) {
            throw new IllegalArgumentException("UserDetails cannot be null or have null username");
        }
        return generateToken(getClaims(userDetails), userDetails, accessTokenExpiration);
    }

    @Override
    public String generateRefreshToken(UserDetails userDetails) {
        if (userDetails == null || userDetails.getUsername() == null) {
            throw new IllegalArgumentException("UserDetails cannot be null or have null username");
        }
        return "REFRESH_" + generateToken(getBasicClaims(userDetails), userDetails, refreshTokenExpiration, jwtRefreshSigningKey);
    }

    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            if (token == null || userDetails == null) {
                return false;
            }
            
            final String userName = extractUserName(token);
            if (userName == null || !userName.equals(userDetails.getUsername())) {
                return false;
            }
            
            // Check if token exists in database and is active
            Token tokenFromDb = tokenRepository.findByToken(token)
                    .orElseThrow(() -> new TokenNotFoundException(token));
                    
            if (!tokenFromDb.getIsActive()) {
                log.warn("Token is not active in database");
                return false;
            }
            
            // Check token expiration
            if (isTokenExpired(token)) {
                log.warn("Token has expired");
                return false;
            }
            
            return true;
            
        } catch (TokenNotFoundException e) {
            log.warn("Token not found in database: {}", token);
            return false;
        } catch (JwtException e) {
            log.error("Error validating JWT token: ", e);
            return false;
        } catch (Exception e) {
            log.error("Unexpected error validating token: ", e);
            return false;
        }
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolvers) {
        final Claims claims = extractAllClaims(token);
        return claimsResolvers.apply(claims);
    }

    private String generateToken(Map<String, Object> extraClaims, UserDetails userDetails, long expiration) {
        return generateToken(extraClaims, userDetails, expiration, jwtSigningKey);
    }

    private String generateToken(Map<String, Object> extraClaims, UserDetails userDetails, long expiration, String signingKey) {
        try {
            Instant now = Instant.now();
            Instant expirationTime = now.plusMillis(expiration);
            
            return Jwts.builder()
                    .setClaims(extraClaims)
                    .setSubject(userDetails.getUsername())
                    .setIssuedAt(Date.from(now))
                    .setExpiration(Date.from(expirationTime))
                    .signWith(getSigningKey(signingKey))
                    .compact();
        } catch (Exception e) {
            log.error("Error generating JWT token: ", e);
            throw new RuntimeException("Failed to generate JWT token", e);
        }
    }

    private boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (JwtException e) {
            log.error("Error checking token expiration: ", e);
            return true; // Treat malformed tokens as expired
        }
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey(jwtSigningKey))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException e) {
            log.error("Error parsing JWT claims: ", e);
            throw e;
        }
    }

    private SecretKey getSigningKey(String key) {
        try {
            byte[] keyBytes = Decoders.BASE64.decode(key);
            return Keys.hmacShaKeyFor(keyBytes);
        } catch (Exception e) {
            log.error("Error creating signing key: ", e);
            throw new RuntimeException("Invalid signing key", e);
        }
    }

    private Map<String, Object> getClaims(UserDetails userDetails) {
        if (!(userDetails instanceof User)) {
            throw new IllegalArgumentException("UserDetails must be an instance of User");
        }
        
        User user = (User) userDetails;
        Map<String, Object> claims = new HashMap<>();
        
        // Add basic user information
        claims.put("username", user.getUsername());
        claims.put("fullName", user.getName());
        claims.put("role", user.getRole().toString());
        
        // Add window information if available
        if (user.getWindow() != null) {
            claims.put("windowId", user.getWindow().getId());
            claims.put("windowNumber", user.getWindow().getWindowNumber());
        } else {
            claims.put("windowId", 0);
            claims.put("windowNumber", "0");
        }
        
        // Add issued at timestamp for additional security
        claims.put("iat", Instant.now().getEpochSecond());
        
        return claims;
    }
    
    private Map<String, Object> getBasicClaims(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", userDetails.getUsername());
        claims.put("iat", Instant.now().getEpochSecond());
        return claims;
    }
}