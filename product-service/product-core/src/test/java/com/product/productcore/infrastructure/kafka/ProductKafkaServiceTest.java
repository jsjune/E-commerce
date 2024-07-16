package com.product.productcore.infrastructure.kafka;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.product.productapi.usecase.InternalProductUseCase;
import com.product.productcore.infrastructure.entity.ProductOutBox;
import com.product.productcore.infrastructure.kafka.event.EventResult;
import com.product.productcore.infrastructure.kafka.event.OrderLineEvent;
import com.product.productcore.infrastructure.repository.ProductOutBoxRepository;
import com.product.productcore.testConfig.IntegrationTestSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;

@RecordApplicationEvents
class ProductKafkaServiceTest extends IntegrationTestSupport {

    @Autowired
    private ApplicationEvents events;
    @MockBean
    private InternalProductUseCase internalProductUseCase;
    @Autowired
    private ProductKafkaService productKafkaService;
    @Autowired
    private ProductOutBoxRepository productOutBoxRepository;

    @BeforeEach
    void setUp() {
        productOutBoxRepository.deleteAllInBatch();
    }

    @DisplayName("재고 감소")
    @Test
    void decrease_stock() {
        // given
        EventResult eventResult = EventResult.builder()
            .orderLine(OrderLineEvent.builder()
                .productId(1L)
                .quantity(1L)
                .build())
            .build();
        when(internalProductUseCase.decreaseStock(eventResult.orderLine().productId(), eventResult.orderLine().quantity())).thenReturn(1);

        // when
        EventResult result = productKafkaService.decreaseStock(eventResult);

        // then
        assertEquals(result.status(), 1);
    }

    @DisplayName("상품 이벤트 처리")
    @Test
    void handle_product() {
        // given
        EventResult eventResult = EventResult.builder()
            .orderLine(OrderLineEvent.builder().build())
            .build();

        // when
        productKafkaService.handleProduct(eventResult);
        long count = events.stream(EventResult.class).count();

        // then
        assertEquals(count, 1);
    }

    @DisplayName("카프카 네트워크 오류로 이벤트를 outbox에 저장")
    @Test
    void test() throws JsonProcessingException {
        // given
        String topic = "product_result";
        EventResult eventResult = EventResult.builder()
            .orderLine(OrderLineEvent.builder().build())
            .build();

        // when
        productKafkaService.occurProductFailure(eventResult);
        ProductOutBox result = productOutBoxRepository.findAll().stream().findFirst().get();

        // then
        assertEquals(result.getTopic(), topic);
    }
}
