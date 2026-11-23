package com.hotelbooking.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.hotelbooking.dto.AvailabilityResponse;
import com.hotelbooking.dto.BookingResponse;
import com.hotelbooking.dto.CreateBookingRequest;
import com.hotelbooking.entity.Hotel;
import com.hotelbooking.entity.RoomType;
import com.hotelbooking.exception.ConflictException;
import com.hotelbooking.exception.NotFoundException;
import com.hotelbooking.repository.HotelRepository;
import com.hotelbooking.repository.RoomTypeRepository;

/**
 * Runs the real services against an in-memory H2 database (MySQL mode) using the same
 * Flyway scripts as production. Each test creates its own small hotel, so tests don't affect each other.
 */
@SpringBootTest
@ActiveProfiles("test")
class BookingFlowIntegrationTest {

    @Autowired private BookingService bookingService;
    @Autowired private HotelService hotelService;
    @Autowired private InventoryService inventoryService;
    @Autowired private BookingRules bookingRules;
    @Autowired private HotelRepository hotelRepository;
    @Autowired private RoomTypeRepository roomTypeRepository;

    private LocalDate day10;

    @BeforeEach
    void setUp() {
        day10 = bookingRules.today().plusDays(10);
    }

    @Test
    void bookingTakesOneRoomAndChargesForEachNight() {
        Long hotelId = createHotelWithRooms(2, "4000.00");

        BookingResponse booking = book(hotelId, day10, day10.plusDays(3));

        assertThat(booking.bookingReference()).matches("HB[A-Z2-9]{8}");
        assertThat(booking.status()).isEqualTo("CONFIRMED");
        assertThat(booking.nights()).isEqualTo(3);
        assertThat(booking.totalAmount()).isEqualByComparingTo("12000.00");
        assertThat(availability(hotelId, day10, day10.plusDays(3)).availableRooms()).isEqualTo(1);
    }

    @Test
    void lastRoomCannotBeBookedTwice() {
        Long hotelId = createHotelWithRooms(1, "3000.00");
        book(hotelId, day10, day10.plusDays(2));

        assertThatThrownBy(() -> book(hotelId, day10, day10.plusDays(2)))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    void stayStartingOnAnotherGuestsCheckOutDayIsAllowedButRealOverlapIsNot() {
        Long hotelId = createHotelWithRooms(1, "3000.00");
        book(hotelId, day10, day10.plusDays(2));                       // nights: 10, 11

        book(hotelId, day10.plusDays(2), day10.plusDays(4));           // nights: 12, 13 -> no clash

        assertThatThrownBy(() -> book(hotelId, day10.plusDays(1), day10.plusDays(3)))  // night 11 is taken
                .isInstanceOf(ConflictException.class);
    }

    @Test
    void failedBookingDoesNotLeaveNightsHalfReserved() {
        Long hotelId = createHotelWithRooms(1, "3000.00");
        book(hotelId, day10.plusDays(2), day10.plusDays(3));           // only night 12 is full

        // nights 10, 11 are free but 12 is full -> whole booking must fail and roll back
        assertThatThrownBy(() -> book(hotelId, day10, day10.plusDays(3)))
                .isInstanceOf(ConflictException.class);

        assertThat(availability(hotelId, day10, day10.plusDays(2)).availableRooms()).isEqualTo(1);
    }

    @Test
    void cancellingReleasesTheRoomAndCannotBeDoneTwice() {
        Long hotelId = createHotelWithRooms(1, "3000.00");
        BookingResponse booking = book(hotelId, day10, day10.plusDays(2));

        BookingResponse cancelled = bookingService.cancelBooking(booking.bookingReference(), "guest@example.com");

        assertThat(cancelled.status()).isEqualTo("CANCELLED");
        assertThat(availability(hotelId, day10, day10.plusDays(2)).availableRooms()).isEqualTo(1);
        book(hotelId, day10, day10.plusDays(2));   // room can be booked again
        assertThatThrownBy(() -> bookingService.cancelBooking(booking.bookingReference(), "guest@example.com"))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    void bookingCanOnlyBeFoundWithTheMatchingEmail() {
        Long hotelId = createHotelWithRooms(1, "3000.00");
        BookingResponse booking = book(hotelId, day10, day10.plusDays(1));

        assertThat(bookingService.getBooking(booking.bookingReference().toLowerCase(), " GUEST@example.com "))
                .extracting(BookingResponse::bookingReference)
                .isEqualTo(booking.bookingReference());
        assertThatThrownBy(() -> bookingService.getBooking(booking.bookingReference(), "someone@else.com"))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void soldOutDatesSuggestTheNearestFreeDates() {
        Long hotelId = createHotelWithRooms(1, "3000.00");
        book(hotelId, day10, day10.plusDays(2));

        AvailabilityResponse response = availability(hotelId, day10, day10.plusDays(2));

        assertThat(response.availableRooms()).isZero();
        assertThat(response.alternativeStays()).isNotEmpty();
        // 2 days earlier (nights 8 and 9) is the closest 2-night stay that avoids nights 10 and 11
        assertThat(response.alternativeStays().get(0).checkIn()).isEqualTo(day10.minusDays(2));
        assertThat(response.alternativeStays()).allSatisfy(alt ->
                assertThat(alt.totalAmount()).isEqualByComparingTo("6000.00"));
    }

    // ---------- helpers ----------

    private Long createHotelWithRooms(int rooms, String price) {
        Hotel hotel = hotelRepository.save(new Hotel("Test Hotel", "Testville", "1 Test Road",
                "Hotel created for a test", Set.of("Free Wi-Fi")));
        roomTypeRepository.save(new RoomType(hotel, "Standard Room", new BigDecimal(price), rooms, 2));
        inventoryService.fillBookingWindow();
        return hotel.getId();
    }

    private BookingResponse book(Long hotelId, LocalDate checkIn, LocalDate checkOut) {
        return bookingService.createBooking(
                new CreateBookingRequest(hotelId, "Test Guest", "guest@example.com", checkIn, checkOut));
    }

    private AvailabilityResponse availability(Long hotelId, LocalDate checkIn, LocalDate checkOut) {
        return hotelService.checkAvailability(hotelId, checkIn, checkOut);
    }
}
