package com.homestay.repository;

import com.homestay.model.Homestay;
import com.homestay.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HomestayRepository extends JpaRepository<Homestay, String> {
    Optional<Homestay> findByEmail(String email);

    @Query("SELECT h FROM Homestay h WHERE h.user = ?1 AND h.status = ?2")
    List<Homestay> findByUserAndStatus(User user, String status);
}
