package com.homestay.repository;

import com.homestay.model.Message;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<Message, String> {
    @Modifying
    @Transactional
    @Query("UPDATE Message m SET m.read = true WHERE m.conversation.id = :conversationId")
    void markMessagesAsRead(String conversationId);
}
