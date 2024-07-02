package com.productservice.usecase.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.productservice.entity.ProductOutBox;
import com.productservice.repository.ProductOutBoxRepository;
import com.productservice.usecase.ProductWriteUseCase;
import com.productservice.usecase.kafka.event.EventResult;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Scheduled;
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
    private final ProductOutBoxRepository outBoxRepository;
    private final KafkaHealthIndicator kafkaHealthIndicator;
    private final ProductWriteUseCase productWriteUseCase;

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

    @Transactional
    public void occurProductFailure(EventResult orderEvent) throws JsonProcessingException {
        String json = objectMapper.writeValueAsString(orderEvent);
        ProductOutBox outBox = ProductOutBox.builder()
            .topic(PRODUCT_TOPIC)
            .message(json)
            .success(false)
            .build();
        outBoxRepository.save(outBox);
    }

    @Transactional
    @Scheduled(fixedRate = 10000)
    public void retry() throws JsonProcessingException {
        log.info("kafka health check and retrying...");
        List<ProductOutBox> findProductOutBoxes = outBoxRepository.findAllBySuccessFalse();
        for (ProductOutBox outBox : findProductOutBoxes) {
            if (kafkaHealthIndicator.isKafkaUp()) {
                EventResult orderEvent = objectMapper.readValue(outBox.getMessage(),
                    EventResult.class);
                int status = productWriteUseCase.decreaseStock(orderEvent.orderLine().productId(),
                    orderEvent.orderLine().quantity());
                orderEvent = orderEvent.withStatus(status);
                this.occurProductEvent(orderEvent);
                outBoxRepository.delete(outBox);
            }
        }
    }
}
