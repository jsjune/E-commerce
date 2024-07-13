package com.deliveryservice.usecase;

import com.deliveryservice.usecase.kafka.DeliveryKafkaProducer;
import com.deliveryservice.usecase.kafka.event.EventResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@Slf4j
@RequiredArgsConstructor
public class DeliveryListener {
    private final DeliveryKafkaProducer deliveryKafkaProducer;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void listenDeliveryEvent(EventResult event) throws JsonProcessingException {
        log.info("Delivery event occurred: {}", event);
        deliveryKafkaProducer.occurDeliveryEvent(event);
    }
}
