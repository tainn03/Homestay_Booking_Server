package com.homestay.repository;

import com.homestay.model.TypeHomestay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TypeHomestayRepository extends JpaRepository<TypeHomestay, String> {
    Optional<TypeHomestay> findByName(String name);

    @Query("SELECT t FROM TypeHomestay t WHERE t.name NOT IN ('Được ưa chuộng', 'Mới', 'Thật ấn tượng')")
    List<TypeHomestay> findAllExceptPopularNewImpressive();
}
