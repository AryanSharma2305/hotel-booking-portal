package com.hotelbooking.exception;

/** HTTP 409 - the request clashes with the current state (no room left, already cancelled). */
public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }
}
