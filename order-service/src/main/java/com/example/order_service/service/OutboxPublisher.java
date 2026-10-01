package com.example.order_service.service;

import com.example.order_service.entity.OutboxEvent;
import com.example.order_service.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OutboxPublisher {

    private final OutboxEventRepository outboxEventRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

   @Scheduled(fixedDelay = 5000)
public void publishEvents() {

    var events = outboxEventRepository.findByPublishedFalse();

    for (OutboxEvent event : events) {

        try {
            kafkaTemplate.send(
                    "order-created",
                    event.getAggregateId().toString(),
                    event.getPayload()
            ).get();

            event.setPublished(true);
            outboxEventRepository.save(event);

        } catch (Exception e) {
            System.out.println(
                    "Failed to publish outbox event: " + event.getId()
            );
        }
    }
}
}