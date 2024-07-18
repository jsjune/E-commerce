package com.product.productscheduler;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.product.productcore.application.service.ProductDecreaseUseCase;
import com.product.productcore.infrastructure.entity.ProductOutBox;
import com.product.productcore.infrastructure.kafka.KafkaHealthIndicator;
import com.product.productcore.infrastructure.kafka.ProductKafkaProducer;
import com.product.productcore.infrastructure.kafka.event.EventResult;
import com.product.productcore.infrastructure.repository.ProductOutBoxRepository;
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
    private ProductOutBoxRepository productOutBoxRepository;
    @MockBean
    private KafkaHealthIndicator kafkaHealthIndicator;
    @MockBean
    private ProductDecreaseUseCase productDecreaseUseCase;
    @MockBean
    private ProductKafkaProducer productKafkaProducer;

    @BeforeEach
    void setUp() {
        productOutBoxRepository.deleteAllInBatch();
    }

    @DisplayName("상품 아웃 박스에 대한 재시도를 수행한다.")
    @Test
    void retry() throws JsonProcessingException {
        // given
        String message = "{\"productOrderId\":1,\"orderLine\":{\"orderLineId\":1,\"productId\":1,\"productName\":\"productName\",\"price\":1000,\"discount\":0,\"quantity\":1},\"memberId\":1,\"paymentMethodId\":1,\"deliveryAddressId\":1,\"paymentId\":1,\"deliveryId\":1,\"status\":1}";
        ProductOutBox outBox = ProductOutBox.builder()
            .topic("topic")
            .message(message)
            .success(false)
            .build();
        productOutBoxRepository.save(outBox);
        when(kafkaHealthIndicator.isKafkaUp()).thenReturn(true);
        when(productDecreaseUseCase.decreaseStock(anyLong(), anyLong())).thenReturn(1);

        // when
        kafkaOutBoxProcessor.retry();
        List<ProductOutBox> result = productOutBoxRepository.findAll();

        // then
        assertEquals(result.size(), 0);
        verify(productKafkaProducer, times(1)).occurProductEvent(any(EventResult.class));
    }
}
