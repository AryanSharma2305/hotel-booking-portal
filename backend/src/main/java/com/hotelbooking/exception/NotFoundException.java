package com.hotelbooking.exception;

/** HTTP 404 - the requested hotel or booking does not exist. */
public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }
}
