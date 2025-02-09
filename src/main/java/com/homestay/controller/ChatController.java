package com.homestay.controller;

import com.homestay.dto.ApiResponse;
import com.homestay.dto.request.SendMessageRequest;
import com.homestay.dto.response.ConversationResponse;
import com.homestay.service.ChatService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@PreAuthorize("hasAnyRole('ADMIN', 'USER', 'LANDLORD')")
public class ChatController {
    ChatService chatService;

    @PostMapping
    public ApiResponse<ConversationResponse> sendMessage(@RequestBody SendMessageRequest request) {
        return ApiResponse.<ConversationResponse>builder()
                .result(chatService.sendMessage(request.getSenderId(), request.getReceiverId(), request.getText()))
                .build();
    }

    @GetMapping("/conversations")
    public ApiResponse<List<ConversationResponse>> getMyConversations(@RequestParam String senderId) {
        return ApiResponse.<List<ConversationResponse>>builder()
                .result(chatService.getAllConversations(senderId))
                .build();
    }

    @GetMapping("/current")
    public ApiResponse<ConversationResponse> getCurrentConversation(@RequestParam String senderId, @RequestParam String receiverId) {
        return ApiResponse.<ConversationResponse>builder()
                .result(chatService.getCurrentConversation(senderId, receiverId))
                .build();
    }
}
