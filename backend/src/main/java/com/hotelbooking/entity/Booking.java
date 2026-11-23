package com.hotelbooking.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "booking")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String bookingReference;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_type_id")
    private RoomType roomType;

    private String guestName;
    private String guestEmail;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private int nights;

    // Price is copied at booking time, so later price changes don't affect this booking
    private BigDecimal pricePerNight;
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime cancelledAt;

    protected Booking() {
        // for JPA
    }

    public Booking(String bookingReference, RoomType roomType, String guestName, String guestEmail,
                   LocalDate checkIn, LocalDate checkOut, int nights,
                   BigDecimal pricePerNight, BigDecimal totalAmount, LocalDateTime createdAt) {
        this.bookingReference = bookingReference;
        this.roomType = roomType;
        this.guestName = guestName;
        this.guestEmail = guestEmail;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.nights = nights;
        this.pricePerNight = pricePerNight;
        this.totalAmount = totalAmount;
        this.createdAt = createdAt;
        this.status = BookingStatus.CONFIRMED;
    }

    public void cancel(LocalDateTime when) {
        this.status = BookingStatus.CANCELLED;
        this.cancelledAt = when;
    }

    public boolean isCancelled() {
        return status == BookingStatus.CANCELLED;
    }

    public Long getId() { return id; }
    public String getBookingReference() { return bookingReference; }
    public RoomType getRoomType() { return roomType; }
    public String getGuestName() { return guestName; }
    public String getGuestEmail() { return guestEmail; }
    public LocalDate getCheckIn() { return checkIn; }
    public LocalDate getCheckOut() { return checkOut; }
    public int getNights() { return nights; }
    public BigDecimal getPricePerNight() { return pricePerNight; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public BookingStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getCancelledAt() { return cancelledAt; }
}
