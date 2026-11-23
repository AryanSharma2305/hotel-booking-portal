package com.hotelbooking.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateBookingRequest(
        @NotNull(message = "Hotel is required")
        Long hotelId,

        @NotBlank(message = "Name is required")
        @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
        String guestName,

        @NotBlank(message = "Email is required")
        @Email(message = "Enter a valid email address")
        @Size(max = 150, message = "Email is too long")
        String guestEmail,

        @NotNull(message = "Check-in date is required")
        LocalDate checkIn,

        @NotNull(message = "Check-out date is required")
        LocalDate checkOut) {
}
