package com.payment.paymentcore.application.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.payment.paymentcore.IntegrationTestSupport;
import com.payment.paymentcore.infrastructure.kafka.PaymentKafkaProducer;
import com.payment.paymentcore.infrastructure.kafka.event.EventResult;
import java.util.EventListener;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

class PaymentListenerTest extends IntegrationTestSupport {

    @Autowired
    private PaymentListener paymentListener;
    @MockBean
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
