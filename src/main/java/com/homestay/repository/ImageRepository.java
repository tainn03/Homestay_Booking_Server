package com.homestay.repository;

import com.homestay.model.Homestay;
import com.homestay.model.Image;
import com.homestay.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepository extends JpaRepository<Image, String> {
    @Modifying // Dùng để chỉ định phương thức này sẽ thay đổi dữ liệu trong cơ sở dữ liệu
    @Query("DELETE FROM Image i WHERE i.homestay = ?1")
    void deleteAllByHomestay(Homestay homestay);

    @Modifying
    @Query("DELETE FROM Image i WHERE i.room = ?1")
    void deleteAllByRoom(Room room);
}
