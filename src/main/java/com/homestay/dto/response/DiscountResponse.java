package com.homestay.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DiscountResponse {
    String id;
    String nameRoom;
    double value;
    String type;
    String description;
    String startDate;
    String endDate;
}
