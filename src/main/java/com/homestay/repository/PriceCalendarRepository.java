package com.homestay.repository;

import com.homestay.model.PriceCalendar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PriceCalendarRepository extends JpaRepository<PriceCalendar, String> {
}
