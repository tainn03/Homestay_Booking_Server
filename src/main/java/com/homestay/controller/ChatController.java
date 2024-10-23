package com.homestay.controller;

import com.homestay.dto.ApiResponse;
import com.homestay.dto.request.SendMessageRequest;
import com.homestay.dto.response.MessageResponse;
import com.homestay.service.ChatService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ChatController {
    ChatService chatService;

    @PostMapping("/send")
    public ApiResponse<String> sendMessage(@RequestBody SendMessageRequest request) {
        chatService.sendMessage(request.getSenderId(), request.getReceiverId(), request.getContent());
        return ApiResponse.<String>builder()
                .code(200)
                .message("Success")
                .result("Message: " + request.getContent() + " sent successfully")
                .build();
    }

    @GetMapping("/current")
    public ApiResponse<List<MessageResponse>> getCurrentChat(@RequestParam String senderId, @RequestParam String receiverId) {
        return ApiResponse.<List<MessageResponse>>builder()
                .code(200)
                .message("Success")
                .result(chatService.getCurrentChat(senderId, receiverId))
                .build();
    }

    @GetMapping("/conversations")
    public ApiResponse<List<String>> getMyChat(@RequestParam String senderId) {
        return ApiResponse.<List<String>>builder()
                .code(200)
                .message("Success")
                .result(chatService.getAllConversations(senderId))
                .build();
    }
}
