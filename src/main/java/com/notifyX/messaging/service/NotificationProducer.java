package com.notifyX.messaging.service;

import com.notifyX.messaging.config.RabbitMQConfig;
import com.notifyX.messaging.constant.NotificationStatus;
import com.notifyX.messaging.constant.NotificationType;
import com.notifyX.messaging.dto.NotificationRequestDto;
import com.notifyX.messaging.entity.Notification;
import com.notifyX.messaging.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class NotificationProducer {

    private final RabbitTemplate rabbitTemplate;
    private final NotificationRepository repository;

    public void publish(NotificationRequestDto request) {

        // ✅ DO NOT set ID manually
        Notification notification = Notification.builder()
                .type(NotificationType.valueOf(request.getType()))
                .recipient(request.getRecipient())
                .subject(request.getSubject())
                .payload(request.getPayload())
                .status(NotificationStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        // ✅ DB generates Long ID
        Notification saved = repository.save(notification);

        // ✅ Send Long ID to RabbitMQ
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.ROUTING_KEY,   // IMPORTANT (not queue name)
                saved.getId()
        );
    }
}