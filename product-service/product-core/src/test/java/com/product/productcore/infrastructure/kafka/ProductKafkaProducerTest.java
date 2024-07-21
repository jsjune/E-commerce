package com.product.productcore.infrastructure.kafka;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.product.productcore.config.log.LoggingProducer;
import com.product.productcore.infrastructure.kafka.event.EventResult;
import com.product.productcore.testConfig.IntegrationTestSupport;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

class ProductKafkaProducerTest extends IntegrationTestSupport {
    @Autowired
    private ProductKafkaProducer productKafkaProducer;
    @MockBean
    private KafkaTemplate<String, String> kafkaTemplate;
    @Mock
    private CompletableFuture<SendResult<String, String>> future;
    @MockBean
    private LoggingProducer loggingProducer;

    @DisplayName("카프카로 상품 이벤트 발생")
    @Test
    void occur_product_event() throws JsonProcessingException {
        // given
        EventResult eventResult = EventResult.builder().build();
        String topic = "product_result";
        when(kafkaTemplate.send(eq(topic), anyString())).thenReturn(future);
        doNothing().when(loggingProducer).sendMessage(anyString(), anyString());

        // when
        productKafkaProducer.occurProductEvent(eventResult);

        // then
        verify(kafkaTemplate, times(1)).send(eq(topic), anyString());

    }
}
