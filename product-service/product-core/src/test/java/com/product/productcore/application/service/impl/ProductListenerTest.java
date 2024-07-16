package com.product.productcore.application.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.product.productcore.application.service.dto.ImageUploadEvent;
import com.product.productcore.application.utils.S3Utils;
import com.product.productcore.infrastructure.entity.Product;
import com.product.productcore.infrastructure.kafka.ProductKafkaProducer;
import com.product.productcore.infrastructure.kafka.event.EventResult;
import com.product.productcore.infrastructure.repository.ProductRepository;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductListenerTest {
    @InjectMocks
    private ProductListener productListener;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private S3Utils s3Utils;
    @Mock
    private ProductKafkaProducer productKafkaProducer;

    private ImageUploadEvent event;

    @BeforeEach
    void setUp() {
        event = new ImageUploadEvent(
            List.of(new byte[]{1, 2, 3}),
            List.of("image1.jpg"),
            List.of("image/jpeg"),
            "companyName",
            1L
        );
    }

    @DisplayName("이벤트를 받아 이미지 업로드 성공")
    @Test
    void listenImageUpload_success() {
        // given
        Product product = Product.builder().build(); // Assume Product has an empty constructor
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        String orgImageUrl = "orgImageUrl";
        String thumbnailUrl = "thumbnailUrl";

        // when
        when(s3Utils.uploadFile(any(), anyLong(), anyString(), anyString())).thenReturn(orgImageUrl);
        when(s3Utils.uploadThumbFile(any(), anyString(), anyString())).thenReturn(thumbnailUrl);
        productListener.listenImageUpload(event);

        // then
        assertEquals(product.getProductImages().size(),1);
        assertEquals(product.getProductImages().get(0).getOrgImageUrl(),orgImageUrl);
        verify(productRepository).save(product);
    }

    @DisplayName("이벤트를 받아 이미지 업로드 - 상품이 없음")
    @Test
    void listenImageUpload_ProductNotFound() {
        // given
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        // when
        productListener.listenImageUpload(event);

        // then
        verify(productRepository, never()).save(any());
    }

    @DisplayName("이벤트를 받아 이미지 업로드 - 실패")
    @Test
    void listenImageUpload_Failure() {
        // given
        Product product = Product.builder().build();
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(s3Utils.uploadFile(any(InputStream.class), anyLong(), anyString(), anyString()))
            .thenThrow(new RuntimeException("Upload error"));

        // when
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            productListener.listenImageUpload(event);
        });

        // then
        assertEquals("이미지 업로드 실패", thrown.getMessage());
        verify(productRepository).delete(product);

    }

    @DisplayName("Product event를 받아 Kafka에 전송 성공")
    @Test
    void listenProductEvent_success() throws JsonProcessingException {
        // given
        EventResult eventResult = EventResult.builder().build();

        // when
        productListener.listenProductEvent(eventResult);

        // then
        verify(productKafkaProducer, times(1)).occurProductEvent(eventResult);
    }

}
