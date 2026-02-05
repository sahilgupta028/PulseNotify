package com.notifyX.messaging.service;

import com.notifyX.messaging.entity.Notification;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final WebClient webClient;
    private final TemplateEngine templateEngine;

    @Value("${email.service.base-path}")
    private String path;

    public void send(Notification notification) {

        String html = buildHtml(notification);

        Map<String, Object> payload = Map.of(
                "to", notification.getRecipient(),
                "subject", notification.getSubject(),
                "html", html
        );

        webClient.post()
                .uri(path)
                .bodyValue(payload)
                .retrieve()
                .toBodilessEntity()
                .block(); // OK (RabbitMQ consumer thread)
    }

    private String buildHtml(Notification notification) {

        Context context = new Context();

        // Inject payload values into template
        notification.getPayload().forEach(context::setVariable);

        // Render Thymeleaf HTML
        return templateEngine.process("email-template", context);
    }
}