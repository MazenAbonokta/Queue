package com.dlj4.tech.queue.exception;

public class TokenNotFoundException extends RuntimeException {
    public TokenNotFoundException(String token) {
        super("Token is invalid!: " + token);
    }
}