package com.payment.paymentcore.application.service.impl;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.payment.paymentcore.infrastructure.kafka.PaymentKafkaProducer;
import com.payment.paymentcore.infrastructure.kafka.event.EventResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PaymentListenerTest {

    @InjectMocks
    private PaymentListener paymentListener;
    @Mock
    private PaymentKafkaProducer paymentKafkaProducer;

    @DisplayName("Payment event를 받아 Kafka에 전송 성공")
    @Test
    void listen_payment_event() throws JsonProcessingException {
        // given
        EventResult eventResult = EventResult.builder().build();

        // when
        paymentListener.listenPaymentEvent(eventResult);

        // then
        verify(paymentKafkaProducer, times(1)).occurPaymentEvent(eventResult);
    }
}
