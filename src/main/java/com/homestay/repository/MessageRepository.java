package com.homestay.repository;

import com.homestay.model.Message;
import com.homestay.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, String> {
    @Query("SELECT m FROM Message m WHERE m.sender = ?1 AND m.receiver = ?2 OR m.sender = ?2 AND m.receiver = ?1")
    List<Message> findBySenderAndReceiver(User sender, User receiver);

    @Query("SELECT DISTINCT m.receiver FROM Message m WHERE m.sender = ?1")
    List<User> findDistinctReceiversBySender(User sender);
}
