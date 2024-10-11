package com.homestay.repository;

import com.homestay.model.District;
import com.homestay.model.Homestay;
import com.homestay.model.TypeHomestay;
import com.homestay.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HomestayRepository extends JpaRepository<Homestay, String> {
    Optional<Homestay> findByEmail(String email);

    @Query("SELECT h FROM Homestay h WHERE h.user = ?1 AND h.status = ?2")
    List<Homestay> findByUserAndStatus(User user, String status);

    @Query("SELECT h FROM Homestay h WHERE h.district.id IN :districtIds OR :typeHomestay MEMBER OF h.typeHomestays")
    List<Homestay> findByDistrictIdInOrTypeHomestay(
            @Param("districtIds") List<Integer> districtIds,
            @Param("typeHomestay") TypeHomestay typeHomestay);

    List<Homestay> findByDistrictIn(List<District> districts);
}
