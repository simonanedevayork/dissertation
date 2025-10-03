package com.york.doghealthtracker.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global exception handler responsible for handling application errors globally.
 * Exceptions thrown in the system are intercepted and mapped to the respective HTTP statuses.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles any FileStorageException exceptions thrown by the application and maps them to a 500 Internal Server
     * Error HTTP status. 404 Not Found HTTP
     * @param ex An FileStorageException thrown by the application.
     * @return a ResponseEntity with status 500 (Internal Server Error)
     */
    @ExceptionHandler(FileStorageException.class)
    public ResponseEntity<String> handleFileStorageException(FileStorageException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }

    /**
     * Handles any ResourceNotFoundException exceptions thrown by the application and maps them to a 404 Not Found HTTP
     * status. Indicates Client Errors.
     * @param ex A FileNotFoundException thrown by the application.
     * @return a ResponseEntity with status 404 (Not Found)
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    /**
     * Handles any IllegalArgumentException exceptions thrown by the application and maps them to a 400 Bad Request
     * HTTP status. Indicates Client Errors.
     * @param ex An IllegalArgumentException thrown by the application.
     * @return a ResponseEntity with status 400 (Bad Request)
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    //TODO: add AuthenticationException

    //TODO: add InvalidDogException

    /**
     * Handles generic exceptions which were not intercepted by the other error handler methods and maps them to a 500
     * Internal Server Error HTTP status.
     * @param ex An exception thrown by the application that was not handled by any of the custom exception handlers.
     * @return a ResponseEntity with status 500 (Internal Server Error)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An unexpected error occurred: " + ex.getMessage());
    }

}
