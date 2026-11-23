package com.hotelbooking.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hotelbooking.entity.RoomInventory;

public interface RoomInventoryRepository extends JpaRepository<RoomInventory, Long> {

    /** Inventory rows for the nights of a stay: check-in included, check-out excluded. */
    @Query("""
            select i from RoomInventory i
            where i.roomType.id = :roomTypeId and i.stayDate >= :checkIn and i.stayDate < :checkOut
            """)
    List<RoomInventory> findNights(@Param("roomTypeId") Long roomTypeId,
                                   @Param("checkIn") LocalDate checkIn,
                                   @Param("checkOut") LocalDate checkOut);

    /**
     * Takes one room on every night of the stay, but only on nights that still have a free room.
     * Returns how many nights were updated. The database locks these rows while updating,
     * so two people can never both get the last room.
     */
    @Modifying
    @Query("""
            update RoomInventory i set i.availableRooms = i.availableRooms - 1
            where i.roomType.id = :roomTypeId and i.stayDate >= :checkIn and i.stayDate < :checkOut
              and i.availableRooms > 0
            """)
    int takeOneRoom(@Param("roomTypeId") Long roomTypeId,
                    @Param("checkIn") LocalDate checkIn,
                    @Param("checkOut") LocalDate checkOut);

    /** Gives one room back on every night of the stay (used when a booking is cancelled). */
    @Modifying
    @Query("""
            update RoomInventory i set i.availableRooms = i.availableRooms + 1
            where i.roomType.id = :roomTypeId and i.stayDate >= :checkIn and i.stayDate < :checkOut
            """)
    int returnOneRoom(@Param("roomTypeId") Long roomTypeId,
                      @Param("checkIn") LocalDate checkIn,
                      @Param("checkOut") LocalDate checkOut);

    @Query("select max(i.stayDate) from RoomInventory i where i.roomType.id = :roomTypeId")
    LocalDate findLastStayDate(@Param("roomTypeId") Long roomTypeId);
}
