package com.product.productconsumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.product.productconsumer.testConfig.IntegrationTestSupport;
import com.productservice.entity.Product;
import com.productservice.repository.ProductRepository;
import com.productservice.usecase.kafka.KafkaHealthIndicator;
import com.productservice.usecase.kafka.ProductKafkaProducer;
import com.productservice.usecase.kafka.event.EventResult;
import com.productservice.usecase.kafka.event.OrderLineEvent;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

public class ProductEventConsumerTest extends IntegrationTestSupport {

    @Autowired
    private ProductEventConsumer productEventConsumer;
    @Autowired
    private ProductRepository productRepository;
    @MockBean
    private ProductKafkaProducer productKafkaProducer;
    @MockBean
    private KafkaHealthIndicator kafkaHealthIndicator;
    @Autowired
    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        productRepository.deleteAllInBatch();
    }

    @DisplayName("product_request 토픽 받는 consumer, 상품 재고 감소")
    @Test
    void consumeProduct() throws JsonProcessingException {
        // given
        Product product = Product.builder()
            .totalStock(100L)
            .soldQuantity(0L)
            .build();
        productRepository.save(product);
        EventResult eventResult = EventResult.builder()
            .orderLine(OrderLineEvent.builder()
                .productId(product.getId())
                .quantity(3L)
                .build())
            .status(0)
            .build();
        String json = objectMapper.writeValueAsString(eventResult);
        ConsumerRecord<String, String> record = new ConsumerRecord<>("product_request", 0, 0, null, json);

        // when
        when(kafkaHealthIndicator.isKafkaUp()).thenReturn(true);
        productEventConsumer.consumeProduct(record);
        Product result = productRepository.findAll().stream().findFirst().get();

        // then
        verify(productKafkaProducer, times(1)).occurProductEvent(eventResult);
        assertEquals(result.getSoldQuantity(), eventResult.orderLine().quantity());
        assertEquals(result.getTotalStock(), 97L);
    }
}
