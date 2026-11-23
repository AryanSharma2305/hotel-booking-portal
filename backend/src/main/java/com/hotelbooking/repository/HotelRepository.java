package com.hotelbooking.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.hotelbooking.entity.Hotel;

public interface HotelRepository extends JpaRepository<Hotel, Long> {

    @Query("select distinct h.location from Hotel h order by h.location")
    List<String> findAllLocations();
}
