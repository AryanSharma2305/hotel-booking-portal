package com.hotelbooking.entity;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/** The room category of a hotel (one per hotel in this version). */
@Entity
@Table(name = "room_type")
public class RoomType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id")
    private Hotel hotel;

    private String name;
    private BigDecimal pricePerNight;
    private int totalRooms;
    private int maxGuests;

    protected RoomType() {
        // for JPA
    }

    public RoomType(Hotel hotel, String name, BigDecimal pricePerNight, int totalRooms, int maxGuests) {
        this.hotel = hotel;
        this.name = name;
        this.pricePerNight = pricePerNight;
        this.totalRooms = totalRooms;
        this.maxGuests = maxGuests;
    }

    public Long getId() { return id; }
    public Hotel getHotel() { return hotel; }
    public String getName() { return name; }
    public BigDecimal getPricePerNight() { return pricePerNight; }
    public int getTotalRooms() { return totalRooms; }
    public int getMaxGuests() { return maxGuests; }
}
