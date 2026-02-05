package com.notifyX.messaging.constant;

public class RabbitMQConstant {
    public static final String EXCHANGE = "notify.exchange";
    public static final String QUEUE = "notify.queue";
    public static final String ROUTING_KEY = "notify.key";

    public static final String RETRY_EXCHANGE = "notify.retry.exchange";
    public static final String RETRY_QUEUE = "notify.retry.queue";

    public static final String DLQ_EXCHANGE = "notify.dlq.exchange";
    public static final String DLQ_QUEUE = "notify.dlq.queue";
}
