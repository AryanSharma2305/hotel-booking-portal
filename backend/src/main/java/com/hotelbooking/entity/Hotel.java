package com.hotelbooking.entity;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "hotel")
public class Hotel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String location;
    private String address;
    private String description;

    @ElementCollection
    @CollectionTable(name = "hotel_amenity", joinColumns = @JoinColumn(name = "hotel_id"))
    @Column(name = "amenity")
    private Set<String> amenities = new HashSet<>();

    protected Hotel() {
        // for JPA
    }

    public Hotel(String name, String location, String address, String description, Set<String> amenities) {
        this.name = name;
        this.location = location;
        this.address = address;
        this.description = description;
        this.amenities = new HashSet<>(amenities);
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getLocation() { return location; }
    public String getAddress() { return address; }
    public String getDescription() { return description; }
    public Set<String> getAmenities() { return amenities; }
}
