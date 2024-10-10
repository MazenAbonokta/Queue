package com.dlj4.tech.queue.exception;

public class RefreshTokenNotFoundException extends RuntimeException {
    public RefreshTokenNotFoundException(String refreshToken) {
        super("Refresh token is invalid!: " + refreshToken);
    }
}