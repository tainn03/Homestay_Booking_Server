package com.homestay.repository;

import com.homestay.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, String> {
    @Query("SELECT r FROM Room r WHERE r.id NOT IN (SELECT b.room.id FROM Booking b WHERE b.checkIn <= ?1 AND b.checkOut >= ?2)")
    List<Room> findAvailableRooms(LocalDate checkIn, LocalDate checkOut);

}
