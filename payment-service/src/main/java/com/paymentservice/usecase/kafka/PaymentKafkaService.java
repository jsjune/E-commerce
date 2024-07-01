package com.paymentservice.usecase.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.paymentservice.usecase.PaymentUseCase;
import com.paymentservice.usecase.kafka.PaymentKafkaProducer;
import com.paymentservice.usecase.kafka.event.EventResult;
import com.paymentservice.usecase.kafka.event.ProductOrderEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PaymentKafkaService {

    private final PaymentUseCase paymentUseCase;
    private final PaymentKafkaProducer paymentKafkaProducer;

    public void handlePayment(ProductOrderEvent orderEvent) throws Exception {
        Long paymentId = paymentUseCase.processPayment(orderEvent.mapToCommand());
        int status = paymentId == -1L ? -1 : 1;
        EventResult eventResult = orderEvent.mapToEventResult(paymentId, status);
        paymentKafkaProducer.occurPaymentEvent(eventResult);
    }

    public void occurPaymentFailure(ProductOrderEvent orderEvent) throws JsonProcessingException {
        paymentKafkaProducer.occurPaymentFailure(orderEvent);
    }
}
