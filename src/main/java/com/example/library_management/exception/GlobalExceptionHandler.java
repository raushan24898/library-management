package com.example.library_management.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntime(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, String>> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("error", "Access denied: " + ex.getMessage()));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, String>> handleBadCredentials(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Invalid credentials"));
    }
    
    /**
     * Handles AWS S3 exceptions.
     * 
     * @param ex the S3Exception
     * @return ResponseEntity with appropriate HTTP status and error message
     */
    @ExceptionHandler(S3Exception.class)
    public ResponseEntity<Map<String, String>> handleS3Exception(S3Exception ex) {
        log.error("S3 Exception occurred: {}", ex.getMessage(), ex);
        
        HttpStatus status;
        String errorMessage;
        
        if (ex instanceof NoSuchKeyException) {
            status = HttpStatus.NOT_FOUND;
            errorMessage = "File not found in S3: " + ex.getMessage();
        } else if (ex.statusCode() == 403) {
            status = HttpStatus.FORBIDDEN;
            errorMessage = "Access denied to S3 resource: " + ex.getMessage();
        } else if (ex.statusCode() == 400) {
            status = HttpStatus.BAD_REQUEST;
            errorMessage = "Invalid S3 request: " + ex.getMessage();
        } else {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            errorMessage = "S3 operation failed: " + ex.getMessage();
        }
        
        return ResponseEntity.status(status)
                .body(Map.of("error", errorMessage));
    }
}
