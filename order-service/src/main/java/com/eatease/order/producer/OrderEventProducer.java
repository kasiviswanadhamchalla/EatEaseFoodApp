package com.eatease.order.producer;

import com.eatease.order.event.OrderEventPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class OrderEventProducer {

    private static final Logger log = LoggerFactory.getLogger(OrderEventProducer.class);
    private static final String TOPIC = "order-events";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public OrderEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(OrderEventPayload payload) {
        log.info("Attempting to send order event to Kafka topic {}: OrderNumber {}", TOPIC, payload.getOrderNumber());

        try {
            CompletableFuture.supplyAsync(() -> {
                kafkaTemplate.send(TOPIC, String.valueOf(payload.getCustomerId()), payload)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            log.info("Successfully published order event for order: {}", payload.getOrderNumber());
                        } else {
                            log.error("Failed to publish order event due to Kafka being unreachable: {}", ex.getMessage());
                        }
                    });
                return null;
            }).exceptionally(ex -> {
                log.error("Async execution failed: {}", ex.getMessage());
                return null;
            });
        } catch (Exception ex) {
            log.error("Immediate failure while publishing event: {}", ex.getMessage());
        }
    }
}
