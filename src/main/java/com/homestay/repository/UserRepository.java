package com.homestay.repository;

import com.homestay.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByEmail(String username);
    List<User> findAllByStatus(String active);
}
