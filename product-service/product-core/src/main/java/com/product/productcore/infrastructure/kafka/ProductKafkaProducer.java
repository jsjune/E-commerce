package com.product.productcore.infrastructure.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.product.productcore.infrastructure.kafka.event.EventResult;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductKafkaProducer {
    @Value(value = "${producers.topic1}")
    private String PRODUCT_TOPIC;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final KafkaTemplate<String, String> kafkaTemplate;

    public void occurProductEvent(EventResult eventResult) throws JsonProcessingException {
        String json = objectMapper.writeValueAsString(eventResult);
        CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(PRODUCT_TOPIC,
            json);
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("[product_request] sent: " + eventResult);
            } else {
                log.error("[product_request] failed to send: " + eventResult + ex);
            }
        });
    }

}
