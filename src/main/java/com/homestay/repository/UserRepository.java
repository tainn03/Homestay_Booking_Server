package com.homestay.repository;

import com.homestay.model.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User, String> {
    @Lock(LockModeType.OPTIMISTIC)
    Optional<User> findByEmail(String username);
}
