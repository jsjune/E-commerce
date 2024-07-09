package com.productservice.usecase.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.productservice.entity.ProductOutBox;
import com.productservice.repository.ProductOutBoxRepository;
import com.productservice.usecase.ProductWriteUseCase;
import com.productservice.usecase.kafka.event.EventResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ProductKafkaService {

    @Value(value = "${producers.topic1}")
    private String PRODUCT_TOPIC;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ProductOutBoxRepository outBoxRepository;
    private final ProductKafkaProducer productKafkaProducer;
    private final ProductWriteUseCase productWriteUseCase;

    public void handleProduct(EventResult orderEvent) throws JsonProcessingException {
        int status = productWriteUseCase.decreaseStock(orderEvent.orderLine().productId(), orderEvent.orderLine().quantity());
        orderEvent = orderEvent.withStatus(status);
        productKafkaProducer.occurProductEvent(orderEvent);
    }

    public void occurProductFailure(EventResult orderEvent) throws JsonProcessingException {
        String json = objectMapper.writeValueAsString(orderEvent);
        ProductOutBox outBox = ProductOutBox.builder()
            .topic(PRODUCT_TOPIC)
            .message(json)
            .success(false)
            .build();
        outBoxRepository.save(outBox);
    }
}
