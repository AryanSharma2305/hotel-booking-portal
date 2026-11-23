package com.hotelbooking.exception;

/** HTTP 400 - the request breaks a booking rule (dates, cancellation window...). */
public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }
}
