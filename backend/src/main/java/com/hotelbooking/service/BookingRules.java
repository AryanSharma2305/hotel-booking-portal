package com.hotelbooking.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.hotelbooking.exception.BadRequestException;

/** Date rules and price calculation for a stay. All pricing happens here, on the backend. */
@Component
public class BookingRules {

    private final Clock clock;
    private final int windowDays;
    private final int maxNights;

    public BookingRules(Clock clock,
                        @Value("${booking.window-days}") int windowDays,
                        @Value("${booking.max-nights}") int maxNights) {
        this.clock = clock;
        this.windowDays = windowDays;
        this.maxNights = maxNights;
    }

    /** Throws a 400 error with a readable message if the dates are not allowed. */
    public void validateDates(LocalDate checkIn, LocalDate checkOut) {
        findDateProblem(checkIn, checkOut).ifPresent(problem -> {
            throw new BadRequestException(problem);
        });
    }

    public boolean areDatesAllowed(LocalDate checkIn, LocalDate checkOut) {
        return findDateProblem(checkIn, checkOut).isEmpty();
    }

    public int countNights(LocalDate checkIn, LocalDate checkOut) {
        return (int) ChronoUnit.DAYS.between(checkIn, checkOut);
    }

    public BigDecimal calculateTotal(BigDecimal pricePerNight, int nights) {
        return pricePerNight.multiply(BigDecimal.valueOf(nights)).setScale(2, RoundingMode.HALF_UP);
    }

    public LocalDate today() {
        return LocalDate.now(clock);
    }

    public LocalDateTime now() {
        return LocalDateTime.now(clock);
    }

    private Optional<String> findDateProblem(LocalDate checkIn, LocalDate checkOut) {
        if (checkIn == null || checkOut == null) {
            return Optional.of("Please select both check-in and check-out dates.");
        }
        LocalDate today = today();
        if (checkIn.isBefore(today)) {
            return Optional.of("Check-in date cannot be in the past.");
        }
        if (!checkOut.isAfter(checkIn)) {
            return Optional.of("Check-out date must be after the check-in date.");
        }
        if (countNights(checkIn, checkOut) > maxNights) {
            return Optional.of("A booking can be for at most " + maxNights + " nights.");
        }
        // the last night of the stay (check-out minus one day) must be inside the booking window
        if (checkOut.isAfter(today.plusDays(windowDays + 1L))) {
            return Optional.of("Bookings can be made up to " + windowDays + " days in advance.");
        }
        return Optional.empty();
    }
}
