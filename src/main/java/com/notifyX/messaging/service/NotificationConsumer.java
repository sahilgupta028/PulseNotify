package com.notifyX.messaging.service;

import com.notifyX.messaging.config.RabbitMQConfig;
import com.notifyX.messaging.constant.NotificationStatus;
import com.notifyX.messaging.entity.Notification;
import com.notifyX.messaging.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationConsumer {

    private final NotificationRepository repository;
    private final RabbitTemplate rabbitTemplate;
    private final EmailService emailService;

    private static final int MAX_RETRY = 3;

    @RabbitListener(queues = RabbitMQConfig.QUEUE)
    public void consume(Long notificationId) {

        Notification notification = repository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        try {
            // 🔔 Send email (or SMS later)
            emailService.send(notification);

            notification.setStatus(NotificationStatus.SENT);
            notification.setUpdatedAt(LocalDateTime.now());
            repository.save(notification);

        } catch (Exception ex) {
            handleFailure(notification, ex);
        }
    }

    private void handleFailure(Notification notification, Exception ex) {

        int retryCount = notification.getRetryCount() + 1;
        notification.setRetryCount(retryCount);
        notification.setUpdatedAt(LocalDateTime.now());

        if (retryCount >= MAX_RETRY) {
            notification.setStatus(NotificationStatus.FAILED);
            repository.save(notification);

            // 💀 Send to DLQ
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.DLQ_EXCHANGE,
                    RabbitMQConfig.ROUTING_KEY,
                    notification.getId()
            );

            log.error("Notification {} moved to DLQ", notification.getId());

        } else {
            notification.setStatus(NotificationStatus.RETRY);
            repository.save(notification);

            // 🔁 Send to retry queue
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.RETRY_EXCHANGE,
                    RabbitMQConfig.ROUTING_KEY,
                    notification.getId()
            );

            log.warn("Retrying notification {} (attempt {})",
                    notification.getId(), retryCount);
        }
    }
}