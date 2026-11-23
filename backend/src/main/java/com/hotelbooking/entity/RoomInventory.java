package com.hotelbooking.entity;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/** Free rooms of one room type on one night. */
@Entity
@Table(name = "room_inventory")
public class RoomInventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_type_id")
    private RoomType roomType;

    private LocalDate stayDate;
    private int availableRooms;

    protected RoomInventory() {
        // for JPA
    }

    public RoomInventory(RoomType roomType, LocalDate stayDate, int availableRooms) {
        this.roomType = roomType;
        this.stayDate = stayDate;
        this.availableRooms = availableRooms;
    }

    public Long getId() { return id; }
    public RoomType getRoomType() { return roomType; }
    public LocalDate getStayDate() { return stayDate; }
    public int getAvailableRooms() { return availableRooms; }
}
