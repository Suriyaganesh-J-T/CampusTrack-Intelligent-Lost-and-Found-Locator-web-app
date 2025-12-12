package com.campus.campus_backend.exception;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<String> handleEmailAlreadyExists(EmailAlreadyExistsException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeExceptions(RuntimeException ex) {
        if (ex.getMessage() != null &&
                (ex.getMessage().contains("Invalid credentials") || ex.getMessage().contains("Account not verified"))) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>("An unexpected server error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
