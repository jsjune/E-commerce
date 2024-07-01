package com.product.productconsumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.productservice.usecase.ProductWriteUseCase;
import com.productservice.usecase.kafka.ProductKafkaProducer;
import com.productservice.usecase.kafka.event.EventResult;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ProductEventConsumer {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ProductWriteUseCase productWriteUseCase;
    private final ProductKafkaProducer productKafkaProducer;

    @Transactional
    @KafkaListener(topics = "${consumers.topic1}", groupId = "${consumers.groupId}")
    public void consumeProduct(ConsumerRecord<String, String> record)
        throws JsonProcessingException {
        EventResult orderEvent = objectMapper.readValue(record.value(), EventResult.class);
        int status = productWriteUseCase.decreaseStock(orderEvent.orderLine().productId(),
            orderEvent.orderLine().quantity());
        orderEvent = orderEvent.withStatus(status);
        productKafkaProducer.occurProductEvent(orderEvent);

    }
}
