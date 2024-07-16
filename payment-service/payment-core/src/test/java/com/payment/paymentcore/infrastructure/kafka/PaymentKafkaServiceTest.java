package com.payment.paymentcore.infrastructure.kafka;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.payment.paymentcore.IntegrationTestSupport;
import com.payment.paymentcore.application.service.PaymentProcessUseCase;
import com.payment.paymentcore.infrastructure.entity.PaymentOutBox;
import com.payment.paymentcore.infrastructure.kafka.event.EventResult;
import com.payment.paymentcore.infrastructure.kafka.event.OrderLineEvent;
import com.payment.paymentcore.infrastructure.kafka.event.ProductOrderEvent;
import com.payment.paymentcore.infrastructure.repository.PaymentOutBoxRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;

@RecordApplicationEvents
class PaymentKafkaServiceTest extends IntegrationTestSupport {

    @Autowired
    private ApplicationEvents events;
    @Autowired
    private PaymentKafkaService paymentKafkaService;
    @MockBean
    private PaymentProcessUseCase paymentProcessUseCase;
    @Autowired
    private PaymentOutBoxRepository paymentOutBoxRepository;

    @BeforeEach
    void setUp() {
        paymentOutBoxRepository.deleteAllInBatch();
    }

    @DisplayName("결제 이벤트 처리")
    @Test
    void handle_payment() throws Exception {
        // given
        ProductOrderEvent productOrderEvent = ProductOrderEvent.builder()
            .orderLine(OrderLineEvent.builder()
                .price(1000L)
                .quantity(1L)
                .build())
            .build();
        when(paymentProcessUseCase.processPayment(productOrderEvent.mapToCommand())).thenReturn(1L);

        // when
        paymentKafkaService.handlePayment(productOrderEvent);
        long count = events.stream(EventResult.class).count();

        // then
        assertEquals(count, 1);
    }

    @DisplayName("카프카 네트워크 오류로 이벤트를 outbox에 저장")
    @Test
    void occur_payment_failure() throws JsonProcessingException {
        // given
        ProductOrderEvent productOrderEvent = ProductOrderEvent.builder()
            .orderLine(OrderLineEvent.builder().build())
            .build();

        // when
        paymentKafkaService.occurPaymentFailure(productOrderEvent);
        PaymentOutBox result = paymentOutBoxRepository.findAll().stream().findFirst().get();

        // then
        assertEquals(result.getTopic(), "payment_result");
    }
}
