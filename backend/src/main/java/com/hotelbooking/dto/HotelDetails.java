package com.hotelbooking.dto;

import java.math.BigDecimal;
import java.util.List;

public record HotelDetails(
        Long id,
        String name,
        String location,
        String address,
        String description,
        List<String> amenities,
        RoomInfo room) {

    public record RoomInfo(String name, BigDecimal pricePerNight, int totalRooms, int maxGuests) {
    }
}
