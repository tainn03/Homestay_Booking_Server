package com.homestay.service;

import com.homestay.dto.response.ConversationResponse;
import com.homestay.dto.response.MessageResponse;
import com.homestay.exception.BusinessException;
import com.homestay.exception.ErrorCode;
import com.homestay.model.BaseEntity;
import com.homestay.model.Conversation;
import com.homestay.model.Message;
import com.homestay.model.User;
import com.homestay.repository.ConversationRepository;
import com.homestay.repository.MessageRepository;
import com.homestay.repository.UserRepository;
import com.homestay.service.external.PusherService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
@Slf4j
public class ChatService {
    PusherService pusherService;
    UserRepository userRepository;
    ConversationRepository conversationRepository;
    MessageRepository messageRepository;

    public ConversationResponse sendMessage(String senderId, String receiverId, String content) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        if (sender.getId().equals(receiver.getId())) {
            return null;
        }
        Conversation conversation = findOrCreateConversation(sender, receiver);

        markMessagesAsRead(conversation.getId());
        Message message = createMessage(sender, receiver, content, conversation);
        conversation.getMessages().add(message);

        sendPusherNotification(conversation.getId(), message);

        conversationRepository.save(conversation);
        return buildConversationResponse(conversation, receiver, content);
    }

    private Conversation findOrCreateConversation(User sender, User receiver) {
        return conversationRepository.findBySenderAndReceiver(sender, receiver)
                .orElseGet(() -> conversationRepository.save(Conversation.builder()
                        .user1(sender)
                        .user2(receiver)
                        .messages(new ArrayList<>())
                        .build()));
    }

    private void markMessagesAsRead(String conversationId) {
        messageRepository.markMessagesAsRead(conversationId);
    }

    private Message createMessage(User sender, User receiver, String content, Conversation conversation) {
        return Message.builder()
                .sender(sender.getUsername())
                .receiver(receiver.getUsername())
                .text(content)
                .conversation(conversation)
                .read(false)
                .build();
    }

    private void sendPusherNotification(String id, Message message) {
        try {
            pusherService.sendMessage("chat", "message-received", id);
        } catch (Exception e) {
            log.error("FAILED TO SEND PUSH NOTIFICATION: {}", e.getMessage());
        }
    }

    private ConversationResponse buildConversationResponse(Conversation conversation, User receiver, String content) {
        return ConversationResponse.builder()
                .id(conversation.getId())
                .userId(receiver.getId())
                .name(receiver.getFullName())
                .email(receiver.getEmail())
                .profilePic(receiver.getAvatar() != null ? receiver.getAvatar().getUrl() : "")
                .lastMessage(content)
                .role(receiver.getRole().getRoleName())
                .messages(conversation.getMessages().stream()
                        .sorted(Comparator.comparing(BaseEntity::getCreatedAt))
                        .map(m -> MessageResponse.builder()
                                .id(m.getId())
                                .sender(m.getSender())
                                .receiver(m.getReceiver())
                                .text(m.getText())
                                .time(m.getCreatedAt()
                                        .format(DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy")))
                                .read(m.isRead())
                                .type(m.getSender().equals(receiver.getUsername()) ? "received" : "sent")
                                .build())
                        .toList())
                .build();
    }

    public List<ConversationResponse> getAllConversations(String senderId) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        List<Conversation> userConversations = conversationRepository.findByUser(sender);
        if (userConversations.isEmpty()) {
            return new ArrayList<>();
        }

        return userConversations.stream()
                .map(c -> {
                    User receiver = c.getUser1().getId().equals(senderId) ? c.getUser2() : c.getUser1();
                    return buildConversationResponse(c, receiver, "");
                })
                .sorted(Comparator.comparing(c -> {
                    if (c.getMessages().isEmpty()) {
                        return null;
                    }
                    String time = c.getMessages().getLast().getTime();
                    return LocalDateTime.parse(time, DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy"));
                }, Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();
    }

    public ConversationResponse getCurrentConversation(String senderId, String receiverId) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        Conversation conversation = conversationRepository.findBySenderAndReceiver(sender, receiver)
                .orElse(null);
        return buildConversationResponse(conversation, receiver, "");
    }
}
