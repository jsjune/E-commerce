package com.paymentservice.usecase.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paymentservice.usecase.PaymentUseCase;
import com.paymentservice.usecase.impl.PaymentRollbackService;
import com.paymentservice.usecase.kafka.event.EventResult;
import com.paymentservice.usecase.kafka.event.ProductOrderEvent;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentEventConsumer {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final PaymentKafkaProducer paymentKafkaProducer;
    private final PaymentUseCase paymentUseCase;
    private final PaymentRollbackService paymentRollbackService;

    @KafkaListener(topics = "${consumers.topic1}", groupId = "${consumers.groupId}")
    public void consumePayment(ConsumerRecord<String, String> record)
        throws Exception {
        ProductOrderEvent orderEvent = objectMapper.readValue(record.value(),
            ProductOrderEvent.class);
        Long paymentId = paymentUseCase.processPayment(orderEvent.mapToCommand());

        EventResult eventResult = EventResult.builder()
            .productOrderId(orderEvent.productOrderId())
            .orderLine(orderEvent.orderLine())
            .memberId(orderEvent.memberId())
            .paymentMethodId(orderEvent.paymentMethodId())
            .deliveryAddressId(orderEvent.deliveryAddressId())
            .paymentId(paymentId)
            .status(paymentId == -1L ? -1 : 1)
            .build();
        paymentKafkaProducer.occurPaymentEvent(eventResult);
    }

    @KafkaListener(topics = "${consumers.topic2}", groupId = "${consumers.groupId}")
    public void consumeRollbackPayment(ConsumerRecord<String, String> record)
        throws Exception {
        EventResult eventResult = objectMapper.readValue(record.value(), EventResult.class);
        paymentRollbackService.rollbackProcessPayment(eventResult.mapToCommand());
    }
}
