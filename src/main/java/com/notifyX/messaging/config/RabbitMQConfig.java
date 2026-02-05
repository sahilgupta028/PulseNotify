package com.notifyX.messaging.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.notifyX.messaging.constant.RabbitMQConstant.*;

@Configuration
public class RabbitMQConfig {

    @Bean
    DirectExchange exchange() {
        return new DirectExchange(EXCHANGE);
    }

    @Bean
    DirectExchange retryExchange() {
        return new DirectExchange(RETRY_EXCHANGE);
    }

    @Bean
    DirectExchange dlqExchange() {
        return new DirectExchange(DLQ_EXCHANGE);
    }

    @Bean
    Queue mainQueue() {
        return QueueBuilder.durable(QUEUE)
                .deadLetterExchange(RETRY_EXCHANGE)
                .deadLetterRoutingKey(ROUTING_KEY)
                .build();
    }

    @Bean
    Queue retryQueue() {
        return QueueBuilder.durable(RETRY_QUEUE)
                .ttl(10000) // ⏱ 10 seconds delay
                .deadLetterExchange(EXCHANGE)
                .deadLetterRoutingKey(ROUTING_KEY)
                .build();
    }

    @Bean
    Queue dlqQueue() {
        return QueueBuilder.durable(DLQ_QUEUE).build();
    }

    @Bean
    Binding mainBinding() {
        return BindingBuilder
                .bind(mainQueue())
                .to(exchange())
                .with(ROUTING_KEY);
    }

    @Bean
    Binding retryBinding() {
        return BindingBuilder
                .bind(retryQueue())
                .to(retryExchange())
                .with(ROUTING_KEY);
    }

    @Bean
    Binding dlqBinding() {
        return BindingBuilder
                .bind(dlqQueue())
                .to(dlqExchange())
                .with(ROUTING_KEY);
    }
}