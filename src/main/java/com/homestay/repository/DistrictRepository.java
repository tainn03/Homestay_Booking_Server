package com.homestay.repository;

import com.homestay.model.City;
import com.homestay.model.District;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DistrictRepository extends JpaRepository<District, Long> {
    Optional<District> findByName(String name);

    @Query("SELECT d FROM District d WHERE d.city IN ?1")
    List<District> findByCityIn(List<City> cities);

    @Query("SELECT d FROM District d WHERE d.city.name = ?1")
    List<District> findByCityName(String cityName);

    @Query("SELECT d FROM District d WHERE d.name = ?1 AND d.city.name = ?2")
    Optional<District> findByNameAndCityName(String districtName, String city);
}
