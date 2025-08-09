package com.dlj4.tech.queue.config;

import com.dlj4.tech.queue.constants.SecurityConstants;
import com.dlj4.tech.queue.service.JwtService;
import com.dlj4.tech.queue.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.apache.commons.lang3.StringUtils;
import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final int BEARER_PREFIX_LENGTH = 7;
    
    private final JwtService jwtService;
    private final UserService userService;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response, 
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        
        try {
            String path = request.getRequestURI();
            
            // Check if the request path is permitted (no authentication required)
            if (isPermittedPath(path)) {
                filterChain.doFilter(request, response);
                return;
            }
            
            // Extract and validate JWT token
            String jwt = extractJwtFromRequest(request);
            if (jwt == null) {
                log.warn("No valid JWT token found in request to: {}", path);
                filterChain.doFilter(request, response);
                return;
            }
            
            // Process JWT token if no authentication is already set
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                authenticateUser(request, jwt);
            }
            
        } catch (Exception e) {
            log.error("Error processing JWT authentication: ", e);
            SecurityContextHolder.clearContext();
        }
        
        filterChain.doFilter(request, response);
    }
    
    private boolean isPermittedPath(String path) {
        return SecurityConstants.PERMITTED_URLS.stream()
                .anyMatch(permittedUrl -> pathMatcher.match(permittedUrl, path));
    }
    
    private String extractJwtFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader(AUTHORIZATION_HEADER);
        
        if (StringUtils.isEmpty(authHeader)) {
            return null;
        }
        
        if (!authHeader.startsWith(BEARER_PREFIX)) {
            log.warn("Authorization header does not start with Bearer prefix");
            return null;
        }
        
        if (authHeader.length() <= BEARER_PREFIX_LENGTH) {
            log.warn("Authorization header Bearer token is empty");
            return null;
        }
        
        return authHeader.substring(BEARER_PREFIX_LENGTH);
    }
    
    private void authenticateUser(HttpServletRequest request, String jwt) {
        try {
            String username = jwtService.extractUserName(jwt);
            
            if (StringUtils.isEmpty(username)) {
                log.warn("Could not extract username from JWT token");
                return;
            }
            
            UserDetails userDetails = userService.userDetailService()
                    .loadUserByUsername(username);
                    
            if (jwtService.isTokenValid(jwt, userDetails)) {
                SecurityContext context = SecurityContextHolder.createEmptyContext();
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                context.setAuthentication(authToken);
                SecurityContextHolder.setContext(context);
                
                log.debug("Successfully authenticated user: {}", username);
            } else {
                log.warn("Invalid JWT token for user: {}", username);
            }
            
        } catch (Exception e) {
            log.error("Error authenticating user with JWT: ", e);
            SecurityContextHolder.clearContext();
        }
    }
}