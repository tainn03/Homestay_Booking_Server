package com.homestay.service.external;

import com.homestay.model.Message;
import com.pusher.rest.Pusher;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.Collections;

import static lombok.AccessLevel.PRIVATE;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class PusherService {
    Pusher pusher;

    public void sendMessage(String channel, String event, Message message) {
        pusher.trigger(channel, event, Collections.singletonMap("message", message.getText()));
    }
}
