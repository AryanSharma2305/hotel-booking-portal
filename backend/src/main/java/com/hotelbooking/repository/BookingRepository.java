package com.hotelbooking.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hotelbooking.entity.Booking;

import jakarta.persistence.LockModeType;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Optional<Booking> findByBookingReference(String bookingReference);

    boolean existsByBookingReference(String bookingReference);

    /** Locks the booking row, so the same booking cannot be cancelled twice at the same time. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select b from Booking b where b.bookingReference = :reference")
    Optional<Booking> findByReferenceForUpdate(@Param("reference") String reference);
}
