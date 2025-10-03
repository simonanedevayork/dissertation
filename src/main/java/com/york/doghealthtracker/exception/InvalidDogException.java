package com.york.doghealthtracker.exception;

public class InvalidDogException extends RuntimeException {
    public InvalidDogException(String message) {
        super(message);
    }

    public InvalidDogException(String message, Throwable cause) {
        super(message, cause);
    }
}
