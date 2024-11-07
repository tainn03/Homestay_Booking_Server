package com.homestay.repository;

import com.homestay.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface BookingRepository extends JpaRepository<Booking, String> {
    List<Booking> findByUserId(String id);

    @Query("SELECT DISTINCT b FROM Booking b JOIN b.rooms r WHERE r.homestay.id IN ?1")
    List<Booking> findByHomestayIds(Set<String> id);
}
