package com.hotelbooking.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hotelbooking.entity.RoomType;

public interface RoomTypeRepository extends JpaRepository<RoomType, Long> {

    Optional<RoomType> findByHotelId(Long hotelId);

    /** Room types with their hotel; location is a case-insensitive partial match ("" = all). */
    @Query("""
            select r from RoomType r join fetch r.hotel h
            where lower(h.location) like lower(concat('%', :location, '%'))
            order by h.name
            """)
    List<RoomType> searchByLocation(@Param("location") String location);
}
