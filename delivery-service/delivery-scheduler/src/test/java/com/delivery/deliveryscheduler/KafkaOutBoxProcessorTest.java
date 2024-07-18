package com.delivery.deliveryscheduler;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.delivery.deliverycore.application.service.DeliveryProcessUseCase;
import com.delivery.deliverycore.infrastructure.entity.DeliveryOutBox;
import com.delivery.deliverycore.infrastructure.kafka.DeliveryKafkaProducer;
import com.delivery.deliverycore.infrastructure.kafka.KafkaHealthIndicator;
import com.delivery.deliverycore.infrastructure.kafka.event.EventResult;
import com.delivery.deliverycore.infrastructure.kafka.event.OrderLineEvent;
import com.delivery.deliverycore.infrastructure.repository.DeliveryOutBoxRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    @Autowired
    private DeliveryOutBoxRepository deliveryOutBoxRepository;
    @MockBean
    private KafkaHealthIndicator kafkaHealthIndicator;
    @MockBean
    private DeliveryProcessUseCase deliveryProcessUseCase;
    @MockBean
    private DeliveryKafkaProducer deliveryKafkaProducer;

    @BeforeEach
    void setUp() {
        deliveryOutBoxRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("배송 아웃 박스에 대한 재시도를 수행한다.")
    void retry() throws Exception {
        // given
        String message = "{\"productOrderId\":1,\"orderLine\":{\"orderLineId\":1,\"productId\":1,\"productName\":\"productName\",\"price\":1000,\"discount\":0,\"quantity\":1},\"memberId\":1,\"paymentMethodId\":1,\"deliveryAddressId\":1,\"paymentId\":1,\"deliveryId\":1,\"status\":1}";
        DeliveryOutBox outBox = DeliveryOutBox.builder()
            .topic("topic")
            .message(message)
            .success(false)
            .build();
        deliveryOutBoxRepository.save(outBox);
        when(kafkaHealthIndicator.isKafkaUp()).thenReturn(true);
        when(deliveryProcessUseCase.processDelivery(any())).thenReturn(1L);

        // when
        kafkaOutBoxProcessor.retry();
        List<DeliveryOutBox> result = deliveryOutBoxRepository.findAll();

        // then
        assertEquals(result.size(), 0);
        verify(deliveryKafkaProducer, times(1)).occurDeliveryEvent(any(EventResult.class));
    }

}
