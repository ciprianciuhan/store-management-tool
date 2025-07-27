package com.ciprian.store_management_tool.controller;

import com.ciprian.store_management_tool.exception.AccessDeniedStoreException;
import com.ciprian.store_management_tool.exception.AuthenticationStoreException;
import com.ciprian.store_management_tool.exception.DuplicateProductException;
import com.ciprian.store_management_tool.exception.StoreExceptionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateProductException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateProductException(DuplicateProductException ex) {
        return ResponseEntity.status(ex.getHttpStatus())
                .body(new ErrorResponse(
                        ex.getMessage(),
                        ex.getExceptionType().name(),
                        ex.getTimestamp()
                ));
    }

    @ExceptionHandler(AccessDeniedStoreException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedStoreException(AccessDeniedStoreException ex) {
        return ResponseEntity.status(ex.getHttpStatus())
                .body(new ErrorResponse(
                        ex.getMessage(),
                        ex.getExceptionType().name(),
                        ex.getTimestamp()
                ));
    }

    @ExceptionHandler(AuthenticationStoreException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationStoreException(AuthenticationStoreException ex) {
        return ResponseEntity.status(ex.getHttpStatus())
                .body(new ErrorResponse(
                        ex.getMessage(),
                        ex.getExceptionType().name(),
                        ex.getTimestamp()
                ));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex) {
        AccessDeniedStoreException storeException = new AccessDeniedStoreException(
                StoreExceptionType.ACCESS_DENIED, ex.getMessage());
        return ResponseEntity.status(storeException.getHttpStatus())
                .body(new ErrorResponse(
                        storeException.getMessage(),
                        storeException.getExceptionType().name(),
                        storeException.getTimestamp()
                ));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException ex) {
        AuthenticationStoreException storeException = new AuthenticationStoreException(
                StoreExceptionType.AUTHENTICATION_REQUIRED, ex.getMessage());
        return ResponseEntity.status(storeException.getHttpStatus())
                .body(new ErrorResponse(
                        storeException.getMessage(),
                        storeException.getExceptionType().name(),
                        storeException.getTimestamp()
                ));
    }

    @Data
    @AllArgsConstructor
    public static class ErrorResponse {
        private String message;
        private String code;
        private LocalDateTime timestamp;
    }
}
