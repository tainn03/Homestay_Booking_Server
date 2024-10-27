package com.homestay.repository;

import com.homestay.model.Conversation;
import com.homestay.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, String> {
    @Query("SELECT DISTINCT c FROM Conversation c WHERE (c.user1 = :sender AND c.user2 = :receiver) OR (c.user1 = :receiver AND c.user2 = :sender)")
    Optional<Conversation> findBySenderAndReceiver(User sender, User receiver);

    @Query("SELECT DISTINCT c FROM Conversation c WHERE c.user1 = :sender OR c.user2 = :sender")
    List<Conversation> findByUser(User sender);
}
