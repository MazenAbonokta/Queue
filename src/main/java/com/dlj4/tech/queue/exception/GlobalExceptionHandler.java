package com.dlj4.tech.queue.exception;


import com.dlj4.tech.queue.dao.response.ResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDto> handleGlobalException(Exception exception,
                                                             WebRequest webRequest) {
        ResponseDto responseDto = new ResponseDto(
                webRequest.getDescription(false),
                HttpStatus.INTERNAL_SERVER_ERROR,
                exception.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(responseDto);
    }
    @ExceptionHandler(ResourceAlreadyExistException.class)
    public ResponseEntity<ResponseDto> ResourceAlreadyExistException(ResourceAlreadyExistException exception,
                                                                            WebRequest webRequest) {
        ResponseDto responseDto = new ResponseDto(
                webRequest.getDescription(false),
                HttpStatus.BAD_REQUEST,
                exception.getMessage(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(responseDto, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ResponseDto> ResourceNotFoundException(ResourceNotFoundException exception,
                                                                          WebRequest webRequest) {
        ResponseDto responseDto = new ResponseDto(
                webRequest.getDescription(false),
                HttpStatus.BAD_REQUEST,
                exception.getMessage(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(responseDto, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(AuthenticationFailedException.class)
    public ResponseEntity<ResponseDto> AuthenticationFailedException(AuthenticationFailedException exception,
                                                                          WebRequest webRequest) {
        ResponseDto responseDto = new ResponseDto(
                webRequest.getDescription(false),
                HttpStatus.UNAUTHORIZED,
                exception.getMessage(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(responseDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
