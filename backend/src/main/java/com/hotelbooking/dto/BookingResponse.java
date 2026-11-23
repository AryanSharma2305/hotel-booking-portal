package com.hotelbooking.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.hotelbooking.entity.Booking;
import com.hotelbooking.entity.Hotel;

public record BookingResponse(
        String bookingReference,
        String status,
        Long hotelId,
        String hotelName,
        String hotelLocation,
        String roomName,
        String guestName,
        String guestEmail,
        LocalDate checkIn,
        LocalDate checkOut,
        int nights,
        BigDecimal pricePerNight,
        BigDecimal totalAmount,
        LocalDateTime createdAt,
        LocalDateTime cancelledAt) {

    public static BookingResponse from(Booking booking) {
        Hotel hotel = booking.getRoomType().getHotel();
        return new BookingResponse(
                booking.getBookingReference(),
                booking.getStatus().name(),
                hotel.getId(),
                hotel.getName(),
                hotel.getLocation(),
                booking.getRoomType().getName(),
                booking.getGuestName(),
                booking.getGuestEmail(),
                booking.getCheckIn(),
                booking.getCheckOut(),
                booking.getNights(),
                booking.getPricePerNight(),
                booking.getTotalAmount(),
                booking.getCreatedAt(),
                booking.getCancelledAt());
    }
}
