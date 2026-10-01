package com.example.notification_service.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderEventConsumer {

    @KafkaListener(
            topics = "order-created",
            groupId = "notification-service"
    )
    public void consumeOrderEvent(String message) {

        System.out.println(
                "Received ORDER_CREATED event: " + message
        );
    }
}