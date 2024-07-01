package com.productservice.usecase.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.productservice.usecase.ProductWriteUseCase;
import com.productservice.usecase.kafka.ProductKafkaProducer;
import com.productservice.usecase.kafka.event.EventResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductKafkaService {

    private final ProductWriteUseCase productWriteUseCase;
    private final ProductKafkaProducer productKafkaProducer;

    public void handleProduct(EventResult orderEvent) throws JsonProcessingException {
        int status = productWriteUseCase.decreaseStock(orderEvent.orderLine().productId(),
            orderEvent.orderLine().quantity());
        orderEvent = orderEvent.withStatus(status);
        productKafkaProducer.occurProductEvent(orderEvent);
    }

    public void occurProductFailure(EventResult orderEvent) throws JsonProcessingException {
        productKafkaProducer.occurProductFailure(orderEvent);
    }
}
