package com.homestay.repository;

import com.homestay.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, String> {
    @Query("SELECT r FROM Room r WHERE r.homestay.id = ?1 " +
            "AND r.id NOT IN (" +
            "   SELECT rb.id FROM Room rb " +
            "   JOIN rb.bookings b " +
            "   WHERE b.checkIn < ?3 AND b.checkOut > ?2" +
            ")")
    List<Room> findAvailableRoomsByHomestayId(String homestayId, LocalDate checkIn, LocalDate checkOut);


    Optional<Room> findByNameAndHomestayId(String name, String homestayId);

}
