package com.dlj4.tech.queue.imp;

import com.dlj4.tech.queue.constants.UserStatus;
import com.dlj4.tech.queue.dao.request.UserRequest;
import com.dlj4.tech.queue.dao.response.UserResponse;
import com.dlj4.tech.queue.entity.Token;
import com.dlj4.tech.queue.entity.User;
import com.dlj4.tech.queue.entity.Window;
import com.dlj4.tech.queue.exception.AuthenticationFailedException;
import com.dlj4.tech.queue.exception.RefreshTokenNotFoundException;
import com.dlj4.tech.queue.exception.ResourceAlreadyExistException;
import com.dlj4.tech.queue.exception.ResourceNotFoundException;
import com.dlj4.tech.queue.mapper.ObjectsDataMapper;
import com.dlj4.tech.queue.repository.TokenRepository;
import com.dlj4.tech.queue.repository.UserRepository;
import com.dlj4.tech.queue.dao.request.RefreshRequest;
import com.dlj4.tech.queue.dao.request.SigningRequest;
import com.dlj4.tech.queue.dao.response.JwtAuthenticationResponse;
import com.dlj4.tech.queue.service.AuthenticationService;
import com.dlj4.tech.queue.service.JwtService;
import com.dlj4.tech.queue.service.UserActionService;
import com.dlj4.tech.queue.service.WindowService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {
    
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final JwtService jwtService;
    private final ObjectsDataMapper objectsDataMapper;
    private final AuthenticationManager authenticationManager;
    private final WindowService windowService;
    private final UserActionService userActionService;
    
    @Value("${token.access.token.expiration}")
    private long accessTokenExpiration;
    @Transactional
    @Override
    public JwtAuthenticationResponse signIn(SigningRequest request) {
        // Validate input
        if (request == null || !StringUtils.hasText(request.getUsername()) || !StringUtils.hasText(request.getPassword())) {
            log.warn("Invalid sign-in request: missing username or password");
            throw new AuthenticationFailedException("Username and password are required");
        }
        
        try {
            // Authenticate user credentials
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername().trim(), request.getPassword()));
                    
            log.info("User authenticated successfully: {}", request.getUsername());
            
        } catch (BadCredentialsException e) {
            log.warn("Authentication failed for user: {}", request.getUsername());
            throw new AuthenticationFailedException("Invalid username or password");
        } catch (AuthenticationException e) {
            log.error("Authentication error for user {}: {}", request.getUsername(), e.getMessage());
            throw new AuthenticationFailedException("Authentication failed");
        }

        // Retrieve user from database
        User user = userRepository.findByUsername(request.getUsername().trim())
                .orElseThrow(() -> {
                    log.error("User not found after successful authentication: {}", request.getUsername());
                    return new AuthenticationFailedException("User not found");
                });

        // Invalidate existing active tokens for security
        invalidateExistingTokens(user);

        // Generate new tokens
        String jwt = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        Date expiresAt = Date.from(Instant.now().plusMillis(accessTokenExpiration));
        
        // Save new token to database
        Token token = new Token(jwt, refreshToken, expiresAt, user, true);
        tokenRepository.save(token);
        
        // Log user action
        userActionService.AddNewAction(user.getUsername(), UserStatus.LOGIN);
        
        log.info("User signed in successfully: {}", user.getUsername());
        
        // Build response
        return JwtAuthenticationResponse.builder()
                .token(jwt)
                .refreshToken(refreshToken)
                .expiresAt(expiresAt)
                .status("success")
                .WindowId(user.getWindow() != null ? user.getWindow().getId() : 0)
                .WindowNumber(user.getWindow() != null ? user.getWindow().getWindowNumber() : "0")
                .Role(user.getRole().toString())
                .build();
    }
    
    private void invalidateExistingTokens(User user) {
        List<Token> activeTokens = tokenRepository.findAllByUserAndIsActive(user, true)
                .orElse(Collections.emptyList());
                
        if (!activeTokens.isEmpty()) {
            log.info("Invalidating {} existing tokens for user: {}", activeTokens.size(), user.getUsername());
            activeTokens.forEach(token -> token.setIsActive(false));
            tokenRepository.saveAll(activeTokens);
        }
    }
    @Override
    public JwtAuthenticationResponse refreshToken(RefreshRequest request) {
        // Validate input
        if (request == null || !StringUtils.hasText(request.getRefreshToken())) {
            log.warn("Invalid refresh token request: missing refresh token");
            throw new RefreshTokenNotFoundException("Refresh token is required");
        }
        
        try {
            // Find token in database
            Token token = tokenRepository.findByRefreshToken(request.getRefreshToken())
                    .orElseThrow(() -> {
                        log.warn("Refresh token not found in database");
                        return new RefreshTokenNotFoundException(request.getRefreshToken());
                    });
            
            // Check if token is valid and active
            if (!token.getIsActive()) {
                log.warn("Refresh token is not active");
                tokenRepository.delete(token);
                throw new RefreshTokenNotFoundException("Token is not active");
            }
            
            if (token.getExpiresAt().before(new Date())) {
                log.warn("Refresh token has expired");
                tokenRepository.delete(token);
                throw new RefreshTokenNotFoundException("Token has expired");
            }
            
            User user = token.getUser();
            if (user == null) {
                log.error("User not found for refresh token");
                tokenRepository.delete(token);
                throw new RefreshTokenNotFoundException("Invalid token");
            }
            
            // Generate new tokens
            String newAccessToken = jwtService.generateToken(user);
            String newRefreshToken = jwtService.generateRefreshToken(user);
            Date expiresAt = Date.from(Instant.now().plusMillis(accessTokenExpiration));
            
            // Invalidate old token and save new one
            token.setIsActive(false);
            tokenRepository.save(token);
            
            Token newToken = new Token(newAccessToken, newRefreshToken, expiresAt, user, true);
            tokenRepository.save(newToken);
            
            log.info("Token refreshed successfully for user: {}", user.getUsername());
            
            return JwtAuthenticationResponse.builder()
                    .token(newAccessToken)
                    .refreshToken(newRefreshToken)
                    .expiresAt(expiresAt)
                    .status("success")
                    .WindowId(user.getWindow() != null ? user.getWindow().getId() : 0)
                    .WindowNumber(user.getWindow() != null ? user.getWindow().getWindowNumber() : "0")
                    .Role(user.getRole().toString())
                    .build();
                    
        } catch (RefreshTokenNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during token refresh: ", e);
            throw new RefreshTokenNotFoundException("Token refresh failed");
        }
    }
    @Override
    public UserResponse signUp(UserRequest userRequest) {

        Window window= null;
        window =userRequest.getWindowId()==""?null:windowService.getWindowByID(Long.parseLong(userRequest.getWindowId()));

        User user = objectsDataMapper.userDTOToUser(userRequest,window);
        Optional<User> checkedUser=userRepository.findByUsername(userRequest.getUsername());
        if(checkedUser.isPresent())
        {
            throw new ResourceAlreadyExistException("User ["+ userRequest.getUsername()+"]");
        }
        user=  userRepository.save(user);
       UserResponse response=objectsDataMapper.userToUserResponse(user);
       return  response;
    }
    @Override
    public void updateUser(UserRequest userRequest) {
        Window window= null;
        window =userRequest.getWindowId()==""?null:windowService.getWindowByID(Long.parseLong(userRequest.getWindowId()));

      User user=userRepository.findByUsername(userRequest.getUsername())
              .orElseThrow(()-> new ResourceNotFoundException("User not Exist"));
user.setWindow(window);
      user=objectsDataMapper.copyUserRequestToUser(user,userRequest);
      userRepository.save(user);
    }
    @Override
    public void deleteUser(Long id) {
       User user= userRepository.findById(id).orElseThrow(
               ()->new ResourceNotFoundException("UserNotFound"));
       user.setDeleted(true);
       userRepository.save(user);
    }

    @Override
    public User findUserByUsername(String username) {

      Optional<User> user=  userRepository.findByUsername(username);
      if(user.isPresent())
      {
          return user.get();
      }
        return null;
    }
}