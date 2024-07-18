package com.order.orderscheduler;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.order.ordercore.infrastructure.entity.OrderOutBox;
import com.order.ordercore.infrastructure.kafka.KafkaHealthIndicator;
import com.order.ordercore.infrastructure.kafka.OrderKafkaService;
import com.order.ordercore.infrastructure.repository.OrderOutBoxRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class KafkaOutBoxProcessorTest {
    @Autowired
    private KafkaOutBoxProcessor kafkaOutBoxProcessor;
    @Autowired
    private OrderOutBoxRepository orderOutBoxRepository;
    @MockBean
    private KafkaHealthIndicator kafkaHealthIndicator;
    @MockBean
    private OrderKafkaService orderKafkaService;

    @BeforeEach
    void setUp() {
        orderOutBoxRepository.deleteAllInBatch();
    }

    @DisplayName("주문 아웃 박스에 대한 재시도를 수행한다.")
    @Test
    void retry() throws JsonProcessingException {
        // given
        String message = "{\"productOrderId\":1,\"orderLine\":{\"orderLineId\":1,\"productId\":1,\"productName\":\"productName\",\"price\":1000,\"discount\":0,\"quantity\":1},\"memberId\":1,\"paymentMethodId\":1,\"deliveryAddressId\":1,\"paymentId\":1,\"deliveryId\":1,\"status\":1}";
        OrderOutBox outBox = OrderOutBox.builder()
            .topic("topic")
            .message(message)
            .success(false)
            .build();
        orderOutBoxRepository.save(outBox);
        when(kafkaHealthIndicator.isKafkaUp()).thenReturn(true);

        // when
        kafkaOutBoxProcessor.retry();
        List<OrderOutBox> result = orderOutBoxRepository.findAll();

        // then
        assertEquals(result.size(), 0);
        verify(orderKafkaService, times(1)).processOutboxMessage(any(OrderOutBox.class));
    }
}
