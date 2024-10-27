package com.homestay.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class ConversationResponse {
    String id;
    String userId;
    String name;
    String email;
    String profilePic;
    String lastMessage;
    String role;
    List<MessageResponse> messages;
}
