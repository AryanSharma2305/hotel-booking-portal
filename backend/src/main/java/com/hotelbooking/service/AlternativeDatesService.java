package com.hotelbooking.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.hotelbooking.dto.AlternativeStay;
import com.hotelbooking.entity.RoomType;

/**
 * Surprise feature: when a hotel is full for the chosen dates, suggest the closest dates
 * (same number of nights, up to a week earlier or later) that still have a room.
 */
@Service
public class AlternativeDatesService {

    private static final int MAX_SHIFT_DAYS = 7;
    private static final int MAX_SUGGESTIONS = 3;

    private final InventoryService inventoryService;
    private final BookingRules bookingRules;

    public AlternativeDatesService(InventoryService inventoryService, BookingRules bookingRules) {
        this.inventoryService = inventoryService;
        this.bookingRules = bookingRules;
    }

    public List<AlternativeStay> findNearestAvailable(RoomType roomType, LocalDate checkIn, LocalDate checkOut) {
        int nights = bookingRules.countNights(checkIn, checkOut);
        List<AlternativeStay> suggestions = new ArrayList<>();

        // try 1 day earlier, 1 day later, 2 days earlier, 2 days later ... so the closest dates come first
        for (int shift = 1; shift <= MAX_SHIFT_DAYS; shift++) {
            for (int direction : new int[] {-1, 1}) {
                LocalDate newCheckIn = checkIn.plusDays((long) shift * direction);
                LocalDate newCheckOut = newCheckIn.plusDays(nights);

                if (bookingRules.areDatesAllowed(newCheckIn, newCheckOut)
                        && inventoryService.availableRooms(roomType.getId(), newCheckIn, newCheckOut) > 0) {
                    suggestions.add(new AlternativeStay(newCheckIn, newCheckOut,
                            bookingRules.calculateTotal(roomType.getPricePerNight(), nights)));
                    if (suggestions.size() == MAX_SUGGESTIONS) {
                        return suggestions;
                    }
                }
            }
        }
        return suggestions;
    }
}
