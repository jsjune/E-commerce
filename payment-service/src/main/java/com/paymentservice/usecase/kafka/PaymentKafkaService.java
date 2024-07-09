package com.paymentservice.usecase.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paymentservice.entity.PaymentOutBox;
import com.paymentservice.repository.PaymentOutBoxRepository;
import com.paymentservice.usecase.PaymentUseCase;
import com.paymentservice.usecase.kafka.event.EventResult;
import com.paymentservice.usecase.kafka.event.ProductOrderEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PaymentKafkaService {
    @Value(value = "${producers.topic1}")
    public String PAYMENT_TOPIC;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final PaymentOutBoxRepository outBoxRepository;
    private final PaymentUseCase paymentUseCase;
    private final PaymentKafkaProducer paymentKafkaProducer;

    public void handlePayment(ProductOrderEvent orderEvent) throws Exception {
        Long paymentId = paymentUseCase.processPayment(orderEvent.mapToCommand());
        int status = paymentId == -1L ? -1 : 1;
        EventResult eventResult = orderEvent.mapToEventResult(paymentId, status);
        paymentKafkaProducer.occurPaymentEvent(eventResult);
    }

    public void occurPaymentFailure(ProductOrderEvent orderEvent) throws JsonProcessingException {
        String json = objectMapper.writeValueAsString(orderEvent);
        PaymentOutBox outBox = PaymentOutBox.builder()
            .topic(PAYMENT_TOPIC)
            .message(json)
            .success(false)
            .build();
        outBoxRepository.save(outBox);
    }
}
