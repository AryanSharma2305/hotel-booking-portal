package com.hotelbooking.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.hotelbooking.entity.RoomInventory;
import com.hotelbooking.entity.RoomType;
import com.hotelbooking.exception.ConflictException;
import com.hotelbooking.repository.RoomInventoryRepository;
import com.hotelbooking.repository.RoomTypeRepository;

/**
 * Keeps track of free rooms per night.
 * A stay from 10th to 12th uses the nights of the 10th and 11th; the 12th is free for the next guest.
 */
@Service
public class InventoryService {

    private final RoomTypeRepository roomTypeRepository;
    private final RoomInventoryRepository inventoryRepository;
    private final BookingRules bookingRules;
    private final int windowDays;

    public InventoryService(RoomTypeRepository roomTypeRepository,
                            RoomInventoryRepository inventoryRepository,
                            BookingRules bookingRules,
                            @Value("${booking.window-days}") int windowDays) {
        this.roomTypeRepository = roomTypeRepository;
        this.inventoryRepository = inventoryRepository;
        this.bookingRules = bookingRules;
        this.windowDays = windowDays;
    }

    /** Adds missing inventory rows so every room type has a row for each night of the booking window. */
    @Transactional
    public void fillBookingWindow() {
        LocalDate today = bookingRules.today();
        LocalDate lastDate = today.plusDays(windowDays);

        for (RoomType roomType : roomTypeRepository.findAll()) {
            LocalDate lastExisting = inventoryRepository.findLastStayDate(roomType.getId());
            LocalDate date = (lastExisting == null || lastExisting.isBefore(today)) ? today : lastExisting.plusDays(1);

            List<RoomInventory> newRows = new ArrayList<>();
            while (!date.isAfter(lastDate)) {
                newRows.add(new RoomInventory(roomType, date, roomType.getTotalRooms()));
                date = date.plusDays(1);
            }
            inventoryRepository.saveAll(newRows);
        }
    }

    /** Rooms free on every night of the stay (the busiest night decides). */
    @Transactional(readOnly = true)
    public int availableRooms(Long roomTypeId, LocalDate checkIn, LocalDate checkOut) {
        List<RoomInventory> nights = inventoryRepository.findNights(roomTypeId, checkIn, checkOut);
        if (nights.size() < bookingRules.countNights(checkIn, checkOut)) {
            return 0; // some night has no inventory row, i.e. outside the booking window
        }
        return nights.stream().mapToInt(RoomInventory::getAvailableRooms).min().orElse(0);
    }

    /**
     * Takes one room for every night of the stay. If even one night is full, it throws,
     * and the caller's transaction rolls back the nights already taken.
     */
    @Transactional(propagation = Propagation.MANDATORY)
    public void reserveRoom(Long roomTypeId, LocalDate checkIn, LocalDate checkOut) {
        int nightsTaken = inventoryRepository.takeOneRoom(roomTypeId, checkIn, checkOut);
        if (nightsTaken != bookingRules.countNights(checkIn, checkOut)) {
            throw new ConflictException("Sorry, no rooms are left for these dates. Please try other dates.");
        }
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void releaseRoom(Long roomTypeId, LocalDate checkIn, LocalDate checkOut) {
        inventoryRepository.returnOneRoom(roomTypeId, checkIn, checkOut);
    }
}
