package com.product.productconsumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.productservice.usecase.kafka.KafkaHealthIndicator;
import com.productservice.usecase.kafka.ProductKafkaService;
import com.productservice.usecase.kafka.event.EventResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductEventConsumer {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ProductKafkaService productKafkaService;
    private final KafkaHealthIndicator kafkaHealthIndicator;

    @KafkaListener(topics = "${consumers.topic1}", groupId = "${consumers.groupId}")
    public void consumeProduct(ConsumerRecord<String, String> record) {
        try {
            EventResult orderEvent = objectMapper.readValue(record.value(), EventResult.class);
            if (kafkaHealthIndicator.isKafkaUp()) {
                productKafkaService.handleProduct(orderEvent);
            } else {
                log.error("Failed to send order event");
                productKafkaService.occurProductFailure(orderEvent);
            }
        } catch (Exception e) {
            log.error("Failed to consume product event");
        }
    }
}
