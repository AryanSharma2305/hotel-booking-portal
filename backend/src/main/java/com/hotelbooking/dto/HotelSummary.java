package com.hotelbooking.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * A hotel in the search results. The availability fields (availableRooms, nights, totalAmount)
 * are only filled when the user searched with dates; otherwise they are null.
 */
public record HotelSummary(
        Long id,
        String name,
        String location,
        String description,
        List<String> amenities,
        String roomName,
        BigDecimal pricePerNight,
        Integer availableRooms,
        Integer nights,
        BigDecimal totalAmount) {
}
