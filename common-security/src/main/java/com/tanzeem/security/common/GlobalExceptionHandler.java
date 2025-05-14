package com.tanzeem.security.common;

import com.tanzeem.security.exception.TokenExpiredException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<String> handleTokenExpiredException(TokenExpiredException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token expired");
    }

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Map<String, String> handleBadCredentialsException(BadCredentialsException ex) {
        return Map.of("error", ex.getMessage());
    }
}
