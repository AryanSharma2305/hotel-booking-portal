package com.hotelbooking.service;

import java.math.BigDecimal;
import java.security.SecureRandom;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hotelbooking.dto.BookingResponse;
import com.hotelbooking.dto.CreateBookingRequest;
import com.hotelbooking.entity.Booking;
import com.hotelbooking.entity.RoomType;
import com.hotelbooking.exception.BadRequestException;
import com.hotelbooking.exception.ConflictException;
import com.hotelbooking.exception.NotFoundException;
import com.hotelbooking.repository.BookingRepository;
import com.hotelbooking.repository.RoomTypeRepository;

@Service
public class BookingService {

    // no 0/O or 1/I, so references are easy to read out on the phone
    private static final String REFERENCE_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final int REFERENCE_LENGTH = 8;

    private final BookingRepository bookingRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final InventoryService inventoryService;
    private final BookingRules bookingRules;
    private final SecureRandom random = new SecureRandom();

    public BookingService(BookingRepository bookingRepository,
                          RoomTypeRepository roomTypeRepository,
                          InventoryService inventoryService,
                          BookingRules bookingRules) {
        this.bookingRepository = bookingRepository;
        this.roomTypeRepository = roomTypeRepository;
        this.inventoryService = inventoryService;
        this.bookingRules = bookingRules;
    }

    @Transactional
    public BookingResponse createBooking(CreateBookingRequest request) {
        bookingRules.validateDates(request.checkIn(), request.checkOut());

        RoomType roomType = roomTypeRepository.findByHotelId(request.hotelId())
                .orElseThrow(() -> new NotFoundException("Hotel not found."));

        // throws if any night is full; the whole transaction is then rolled back
        inventoryService.reserveRoom(roomType.getId(), request.checkIn(), request.checkOut());

        int nights = bookingRules.countNights(request.checkIn(), request.checkOut());
        BigDecimal total = bookingRules.calculateTotal(roomType.getPricePerNight(), nights);

        Booking booking = new Booking(
                newReference(),
                roomType,
                request.guestName().trim(),
                normaliseEmail(request.guestEmail()),
                request.checkIn(),
                request.checkOut(),
                nights,
                roomType.getPricePerNight(),
                total,
                bookingRules.now());
        bookingRepository.save(booking);
        return BookingResponse.from(booking);
    }

    @Transactional(readOnly = true)
    public BookingResponse getBooking(String reference, String email) {
        Booking booking = bookingRepository.findByBookingReference(normaliseReference(reference))
                .filter(b -> b.getGuestEmail().equals(normaliseEmail(email)))
                .orElseThrow(BookingService::bookingNotFound);
        return BookingResponse.from(booking);
    }

    @Transactional
    public BookingResponse cancelBooking(String reference, String email) {
        Booking booking = bookingRepository.findByReferenceForUpdate(normaliseReference(reference))
                .filter(b -> b.getGuestEmail().equals(normaliseEmail(email)))
                .orElseThrow(BookingService::bookingNotFound);

        if (booking.isCancelled()) {
            throw new ConflictException("This booking is already cancelled.");
        }
        if (bookingRules.today().isAfter(booking.getCheckIn())) {
            throw new BadRequestException("This stay has already started, so it can no longer be cancelled online.");
        }

        booking.cancel(bookingRules.now());
        inventoryService.releaseRoom(booking.getRoomType().getId(), booking.getCheckIn(), booking.getCheckOut());
        return BookingResponse.from(booking);
    }

    private String newReference() {
        String reference;
        do {
            StringBuilder sb = new StringBuilder("HB");
            for (int i = 0; i < REFERENCE_LENGTH; i++) {
                sb.append(REFERENCE_CHARS.charAt(random.nextInt(REFERENCE_CHARS.length())));
            }
            reference = sb.toString();
        } while (bookingRepository.existsByBookingReference(reference));
        return reference;
    }

    private static NotFoundException bookingNotFound() {
        // same message for "wrong reference" and "wrong email", so nobody can guess other people's bookings
        return new NotFoundException("No booking found for this reference and email.");
    }

    private static String normaliseReference(String reference) {
        return reference == null ? "" : reference.trim().toUpperCase();
    }

    private static String normaliseEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase();
    }
}
