package com.payment.paymentcore.infrastructure.kafka;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payment.paymentcore.application.service.PaymentProcessUseCase;
import com.payment.paymentcore.infrastructure.entity.PaymentOutBox;
import com.payment.paymentcore.infrastructure.kafka.event.EventResult;
import com.payment.paymentcore.infrastructure.kafka.event.ProductOrderEvent;
import com.payment.paymentcore.infrastructure.repository.PaymentOutBoxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
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
    private final PaymentProcessUseCase paymentProcessUseCase;
    private final ApplicationEventPublisher eventPublisher;

    public void handlePayment(ProductOrderEvent orderEvent) throws Exception {
        Long paymentId = paymentProcessUseCase.processPayment(orderEvent.mapToCommand());
        int status = paymentId == -1L ? -1 : 1;
        EventResult eventResult = orderEvent.mapToEventResult(paymentId, status);
        eventPublisher.publishEvent(eventResult);
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
