package com.paymentservice.usecase;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.paymentservice.usecase.kafka.PaymentKafkaProducer;
import com.paymentservice.usecase.kafka.event.EventResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@Slf4j
@RequiredArgsConstructor
public class PaymentListener {

    private final PaymentKafkaProducer paymentKafkaProducer;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void listenPaymentEvent(EventResult event) throws JsonProcessingException {
        log.info("Payment event occurred: {}", event);
        paymentKafkaProducer.occurPaymentEvent(event);
    }
}
