package com.homestay.service;

import com.homestay.repository.HomestayRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class HomestayService {
    HomestayRepository homestayRepository;
}
