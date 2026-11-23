package com.hotelbooking.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hotelbooking.dto.AvailabilityResponse;
import com.hotelbooking.dto.HotelDetails;
import com.hotelbooking.dto.HotelSummary;
import com.hotelbooking.service.HotelService;

@RestController
@RequestMapping("/api/hotels")
public class HotelController {

    private final HotelService hotelService;

    public HotelController(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    /** GET /api/hotels?location=Goa&checkIn=2026-12-10&checkOut=2026-12-12 (all parameters optional) */
    @GetMapping
    public List<HotelSummary> searchHotels(
            @RequestParam(required = false) String location,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut) {
        return hotelService.searchHotels(location, checkIn, checkOut);
    }

    @GetMapping("/locations")
    public List<String> getLocations() {
        return hotelService.getLocations();
    }

    @GetMapping("/{hotelId}")
    public HotelDetails getHotel(@PathVariable Long hotelId) {
        return hotelService.getHotel(hotelId);
    }

    /** GET /api/hotels/3/availability?checkIn=2026-12-10&checkOut=2026-12-12 */
    @GetMapping("/{hotelId}/availability")
    public AvailabilityResponse checkAvailability(
            @PathVariable Long hotelId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut) {
        return hotelService.checkAvailability(hotelId, checkIn, checkOut);
    }
}
