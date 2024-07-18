package com.payment.paymentscheduler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payment.paymentcore.application.service.PaymentProcessUseCase;
import com.payment.paymentcore.application.service.dto.ProcessPaymentDto;
import com.payment.paymentcore.infrastructure.entity.PaymentOutBox;
import com.payment.paymentcore.infrastructure.kafka.KafkaHealthIndicator;
import com.payment.paymentcore.infrastructure.kafka.PaymentKafkaProducer;
import com.payment.paymentcore.infrastructure.kafka.event.EventResult;
import com.payment.paymentcore.infrastructure.kafka.event.OrderLineEvent;
import com.payment.paymentcore.infrastructure.kafka.event.ProductOrderEvent;
import com.payment.paymentcore.infrastructure.repository.PaymentOutBoxRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class KafkaOutBoxProcessorTest {
    @Autowired
    private KafkaOutBoxProcessor kafkaOutBoxProcessor;
    @MockBean
    private PaymentKafkaProducer paymentKafkaProducer;
    @MockBean
    private PaymentProcessUseCase paymentProcessUseCase;
    @MockBean
    private KafkaHealthIndicator kafkaHealthIndicator;
    @Autowired
    private PaymentOutBoxRepository paymentOutBoxRepository;

    @BeforeEach
    void setUp() {
        paymentOutBoxRepository.deleteAllInBatch();
    }

    @DisplayName("결제 아웃 박스에 대한 재시도를 수행한다.")
    @Test
    void retry() throws Exception {
        // given
        String message = "{\"productOrderId\":1,\"orderLine\":{\"orderLineId\":1,\"productId\":null,\"productName\":null,\"price\":1000,\"quantity\":1,\"discount\":0},\"memberId\":1,\"paymentMethodId\":1,\"deliveryAddressId\":1}";
        PaymentOutBox outBox = PaymentOutBox.builder()
            .topic("topic")
            .message(message)
            .success(false)
            .build();
        paymentOutBoxRepository.save(outBox);
        when(kafkaHealthIndicator.isKafkaUp()).thenReturn(true);
        when(paymentProcessUseCase.processPayment(any(ProcessPaymentDto.class))).thenReturn(1L);

        // when
        kafkaOutBoxProcessor.retry();
        List<PaymentOutBox> result = paymentOutBoxRepository.findAll();

        // then
        assertEquals(result.size(), 0);
        verify(paymentKafkaProducer, times(1)).occurPaymentEvent(any(EventResult.class));
    }

}
