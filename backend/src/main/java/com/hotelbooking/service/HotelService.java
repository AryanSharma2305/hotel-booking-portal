package com.hotelbooking.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hotelbooking.dto.AlternativeStay;
import com.hotelbooking.dto.AvailabilityResponse;
import com.hotelbooking.dto.HotelDetails;
import com.hotelbooking.dto.HotelSummary;
import com.hotelbooking.entity.Hotel;
import com.hotelbooking.entity.RoomType;
import com.hotelbooking.exception.NotFoundException;
import com.hotelbooking.repository.HotelRepository;
import com.hotelbooking.repository.RoomTypeRepository;

@Service
@Transactional(readOnly = true)
public class HotelService {

    private final HotelRepository hotelRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final InventoryService inventoryService;
    private final AlternativeDatesService alternativeDatesService;
    private final BookingRules bookingRules;

    public HotelService(HotelRepository hotelRepository,
                        RoomTypeRepository roomTypeRepository,
                        InventoryService inventoryService,
                        AlternativeDatesService alternativeDatesService,
                        BookingRules bookingRules) {
        this.hotelRepository = hotelRepository;
        this.roomTypeRepository = roomTypeRepository;
        this.inventoryService = inventoryService;
        this.alternativeDatesService = alternativeDatesService;
        this.bookingRules = bookingRules;
    }

    public List<String> getLocations() {
        return hotelRepository.findAllLocations();
    }

    /** Hotels matching the location. If dates are given, each result also shows free rooms and total price. */
    public List<HotelSummary> searchHotels(String location, LocalDate checkIn, LocalDate checkOut) {
        boolean hasDates = checkIn != null || checkOut != null;
        if (hasDates) {
            bookingRules.validateDates(checkIn, checkOut);
        }
        String locationFilter = location == null ? "" : location.trim();

        return roomTypeRepository.searchByLocation(locationFilter).stream()
                .map(roomType -> toSummary(roomType, hasDates ? checkIn : null, hasDates ? checkOut : null))
                .toList();
    }

    public HotelDetails getHotel(Long hotelId) {
        RoomType roomType = findRoomType(hotelId);
        Hotel hotel = roomType.getHotel();
        return new HotelDetails(
                hotel.getId(),
                hotel.getName(),
                hotel.getLocation(),
                hotel.getAddress(),
                hotel.getDescription(),
                sortedAmenities(hotel),
                new HotelDetails.RoomInfo(roomType.getName(), roomType.getPricePerNight(),
                        roomType.getTotalRooms(), roomType.getMaxGuests()));
    }

    public AvailabilityResponse checkAvailability(Long hotelId, LocalDate checkIn, LocalDate checkOut) {
        bookingRules.validateDates(checkIn, checkOut);
        RoomType roomType = findRoomType(hotelId);

        int nights = bookingRules.countNights(checkIn, checkOut);
        int availableRooms = inventoryService.availableRooms(roomType.getId(), checkIn, checkOut);
        List<AlternativeStay> alternatives = availableRooms > 0
                ? List.of()
                : alternativeDatesService.findNearestAvailable(roomType, checkIn, checkOut);

        return new AvailabilityResponse(
                hotelId,
                checkIn,
                checkOut,
                nights,
                roomType.getPricePerNight(),
                bookingRules.calculateTotal(roomType.getPricePerNight(), nights),
                availableRooms,
                alternatives);
    }

    private HotelSummary toSummary(RoomType roomType, LocalDate checkIn, LocalDate checkOut) {
        Hotel hotel = roomType.getHotel();
        Integer availableRooms = null;
        Integer nights = null;
        BigDecimal totalAmount = null;

        if (checkIn != null) {
            nights = bookingRules.countNights(checkIn, checkOut);
            availableRooms = inventoryService.availableRooms(roomType.getId(), checkIn, checkOut);
            totalAmount = bookingRules.calculateTotal(roomType.getPricePerNight(), nights);
        }

        return new HotelSummary(
                hotel.getId(),
                hotel.getName(),
                hotel.getLocation(),
                hotel.getDescription(),
                sortedAmenities(hotel),
                roomType.getName(),
                roomType.getPricePerNight(),
                availableRooms,
                nights,
                totalAmount);
    }

    private RoomType findRoomType(Long hotelId) {
        return roomTypeRepository.findByHotelId(hotelId)
                .orElseThrow(() -> new NotFoundException("Hotel not found."));
    }

    private List<String> sortedAmenities(Hotel hotel) {
        return hotel.getAmenities().stream().sorted().toList();
    }
}
