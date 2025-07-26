package com.ciprian.store_management_tool.service;

import com.ciprian.store_management_tool.dto.ProductCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductEventPublisher {

    private final KafkaTemplate<String, ProductCreatedEvent> kafkaTemplate;
    private static final String TOPIC = "product-events";

    public void publish(ProductCreatedEvent event) {
        log.info("Publishing event to Kafka: {}", event);
        kafkaTemplate.send(TOPIC, event);
    }
}
