package com.notifyX.messaging.controller;

import com.notifyX.messaging.dto.NotificationRequestDto;
import com.notifyX.messaging.service.NotificationProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationProducer producer;

    @PostMapping
    public ResponseEntity<String> send(@RequestBody NotificationRequestDto request) {
        producer.publish(request);
        return ResponseEntity.accepted().body("Notification queued");
    }
}