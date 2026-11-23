package com.hotelbooking.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Availability and price for a stay. When no room is free, alternativeStays holds
 * the nearest dates (same number of nights) that do have a room.
 */
public record AvailabilityResponse(
        Long hotelId,
        LocalDate checkIn,
        LocalDate checkOut,
        int nights,
        BigDecimal pricePerNight,
        BigDecimal totalAmount,
        int availableRooms,
        List<AlternativeStay> alternativeStays) {
}
