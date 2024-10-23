package com.homestay.service;

import com.homestay.dto.response.MessageResponse;
import com.homestay.model.Message;
import com.homestay.model.User;
import com.homestay.repository.MessageRepository;
import com.homestay.repository.UserRepository;
import com.homestay.service.external.PusherService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class ChatService {
    PusherService pusherService;
    UserRepository userRepository;
    MessageRepository messageRepository;

    public void sendMessage(String senderId, String receiverId, String content) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Sender not found"));
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        Message message = Message.builder()
                .sender(sender)
                .receiver(receiver)
                .content(content)
                .seen(false)
                .build();
        messageRepository.save(message);

        // Trigger the event in Pusher
        pusherService.sendMessage("private-" + receiver.getId(), "message-received", message);
    }

    public List<MessageResponse> getCurrentChat(String senderId, String receiverId) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Sender not found"));
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        List<Message> messages = messageRepository.findBySenderAndReceiver(sender, receiver);
        return messages.stream()
                .map(message -> MessageResponse.builder()
                        .id(message.getId())
                        .senderName(message.getSender().getUsername())
                        .receiverName(message.getReceiver().getUsername())
                        .content(message.getContent())
                        .seen(message.isSeen())
                        .createdAt(message.getCreatedAt())
                        .build())
                .toList();
    }


    // Lấy tất cả các người dùng mà người dùng hiện tại đã trò chuyện
    public List<String> getAllConversations(String senderId) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Sender not found"));

        return messageRepository.findDistinctReceiversBySender(sender)
                .stream()
                .map(User::getUsername)
                .toList();
    }
}
