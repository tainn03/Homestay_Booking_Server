package com.homestay.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DiscountRequest {
    String id;
    int value;
    String description;
    String type;
    LocalDateTime startDate;
    LocalDateTime endDate;
}
