package com.homestay.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReviewResponse {
    String id;
    Integer rating;
    String comment;
    String userName;
    String avatar;
    String homestayId;
}
