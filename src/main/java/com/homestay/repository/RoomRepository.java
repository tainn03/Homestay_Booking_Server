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
    @Query("SELECT r FROM Room r WHERE r.id NOT IN (SELECT b.room.id FROM Booking b WHERE b.checkIn <= ?1 AND b.checkOut >= ?2)")
    List<Room> findAvailableRooms(LocalDate checkIn, LocalDate checkOut);

    @Query("SELECT r FROM Room r WHERE r.homestay.id = ?1 AND r.id NOT IN (SELECT b.room.id FROM Booking b WHERE b.checkIn <= ?2 AND b.checkOut >= ?3)")
    List<Room> findAvailableRoomsByHomestayId(String homestayId, LocalDate checkIn, LocalDate checkOut);

    Optional<Room> findByNameAndHomestayId(String name, String homestayId);

}
