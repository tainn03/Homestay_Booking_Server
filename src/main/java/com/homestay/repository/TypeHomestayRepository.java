package com.homestay.repository;

import com.homestay.model.TypeHomestay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TypeHomestayRepository extends JpaRepository<TypeHomestay, String> {
    Optional<TypeHomestay> findByName(String name);
}
