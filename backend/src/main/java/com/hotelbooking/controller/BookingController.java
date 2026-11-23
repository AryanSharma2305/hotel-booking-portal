package com.hotelbooking.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.hotelbooking.dto.BookingResponse;
import com.hotelbooking.dto.CancelBookingRequest;
import com.hotelbooking.dto.CreateBookingRequest;
import com.hotelbooking.service.BookingService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponse createBooking(@Valid @RequestBody CreateBookingRequest request) {
        return bookingService.createBooking(request);
    }

    /** GET /api/bookings/HB7K2M9QXR?email=guest@example.com */
    @GetMapping("/{reference}")
    public BookingResponse getBooking(@PathVariable String reference, @RequestParam String email) {
        return bookingService.getBooking(reference, email);
    }

    /** Cancelling keeps the booking (status CANCELLED) for history, so this is a POST action, not a DELETE. */
    @PostMapping("/{reference}/cancel")
    public BookingResponse cancelBooking(@PathVariable String reference,
                                         @Valid @RequestBody CancelBookingRequest request) {
        return bookingService.cancelBooking(reference, request.email());
    }
}
