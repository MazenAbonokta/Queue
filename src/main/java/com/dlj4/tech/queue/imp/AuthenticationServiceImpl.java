package com.dlj4.tech.queue.imp;
import com.dlj4.tech.queue.dao.request.UserDAO;
import com.dlj4.tech.queue.entity.Token;
import com.dlj4.tech.queue.exception.RefreshTokenNotFoundException;
import com.dlj4.tech.queue.repository.TokenRepository;
import com.dlj4.tech.queue.repository.UserRepository;
import com.dlj4.tech.queue.dao.request.RefreshRequest;
import com.dlj4.tech.queue.dao.request.SigningRequest;
import com.dlj4.tech.queue.dao.response.JwtAuthenticationResponse;
import com.dlj4.tech.queue.service.AuthenticationService;
import com.dlj4.tech.queue.service.JwtService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    @Value("${token.access.token.expiration}")
    private long accessTokenExpiration;

    @Transactional
    @Override
    public JwtAuthenticationResponse signIn(SigningRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password."));
        List<Token> activeTokens = tokenRepository.findAllByUserAndIsActive(user, true).orElse(Collections.emptyList());
        if (!activeTokens.isEmpty()) {
            for (Token activeToken : activeTokens) {
                activeToken.setIsActive(false);
            }
            tokenRepository.saveAll(activeTokens);
        }
        var jwt = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        var expiresAt = new Date(System.currentTimeMillis() + accessTokenExpiration);
        tokenRepository.save(new Token(jwt, refreshToken, expiresAt, user, true));
        return JwtAuthenticationResponse.builder().token(jwt).refreshToken(refreshToken).expiresAt(expiresAt).build();
    }

    @Override
    public JwtAuthenticationResponse refreshToken(RefreshRequest request) {
        Token token = tokenRepository.findByRefreshToken(request.getRefreshToken()).orElseThrow(() -> new RefreshTokenNotFoundException(request.getRefreshToken()));
        if (token.getExpiresAt().after(new Date()) && token.getIsActive()) {
            var user = token.getUser();
            var newAccessToken = jwtService.generateToken(user);
            var newRefreshToken = jwtService.generateRefreshToken(user);
            var expiresAt = new Date(System.currentTimeMillis() + accessTokenExpiration);
            token.setIsActive(false);
            tokenRepository.save(token);
            tokenRepository.save(new Token(newAccessToken, newRefreshToken, expiresAt, user, true));
            return new JwtAuthenticationResponse(newAccessToken, newRefreshToken, expiresAt);

        } else {
            tokenRepository.delete(token);
            return null;
        }
    }

    @Override
    public void signUp(UserDAO userDAO) {

    }
}