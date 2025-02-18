package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<?> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)  // 409 Conflict
                .body(Map.of("errorCode", ex.getErrorCode(), "errorMessage", ex.getErrorMessage()));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<?> handleUserNotFoundException(UserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)  // 404 Not Found
                .body(Map.of("errorCode", ex.getErrorCode(), "errorMessage", ex.getErrorMessage()));
    }

    @ExceptionHandler(InvalidLoginCredentialsException.class)
    public ResponseEntity<?> handleInvalidLoginCredentialsException(InvalidLoginCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)  // 401 Unauthorized
                .body(Map.of("errorCode", ex.getErrorCode(), "errorMessage", ex.getErrorMessage()));
    }

    // General fallback for any other exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneralException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An unexpected error occurred: " + ex.getMessage());
    }
}
