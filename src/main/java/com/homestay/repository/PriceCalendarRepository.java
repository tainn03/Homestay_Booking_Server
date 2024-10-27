package com.homestay.repository;

import com.homestay.model.Homestay;
import com.homestay.model.PriceCalendar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface PriceCalendarRepository extends JpaRepository<PriceCalendar, String> {
    @Modifying
    @Transactional
    void deleteAllByHomestay(Homestay homestay);
}
