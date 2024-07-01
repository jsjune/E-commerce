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

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductEventConsumer {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ProductKafkaService productKafkaService;
    private final KafkaHealthIndicator kafkaHealthIndicator;

    @KafkaListener(topics = "${consumers.topic1}", groupId = "${consumers.groupId}")
    public void consumeProduct(ConsumerRecord<String, String> record)
        throws JsonProcessingException {
        EventResult orderEvent = objectMapper.readValue(record.value(), EventResult.class);
        if (kafkaHealthIndicator.isKafkaUp()) {
            productKafkaService.handleProduct(orderEvent);
        } else {
            log.error("Failed to send order event");
            productKafkaService.occurProductFailure(orderEvent);
        }
    }
}
