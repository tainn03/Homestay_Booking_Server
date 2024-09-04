package com.homestay.repository;

import com.homestay.model.Homestay;
import com.homestay.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

@Repository
public interface HomestayRepository extends JpaRepository<Homestay, String> {
    @Query("SELECT h FROM Homestay h WHERE h.user = ?1")
    List<Homestay> findByUser(User user);

    Arrays findByUserId(String userId);
}
