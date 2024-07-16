package com.orderservice.usecase.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.orderservice.IntegrationTestSupport;
import com.orderservice.adapter.DeliveryClient;
import com.orderservice.adapter.MemberClient;
import com.orderservice.adapter.ProductClient;
import com.orderservice.usecase.dto.CartDto;
import com.orderservice.usecase.dto.ProductDto;
import com.orderservice.infrastructure.entity.OrderLine;
import com.orderservice.infrastructure.entity.OrderLineStatus;
import com.orderservice.infrastructure.entity.ProductOrderStatus;
import com.orderservice.infrastructure.entity.ProductOrder;
import com.orderservice.infrastructure.repository.OrderLineRepository;
import com.orderservice.infrastructure.repository.ProductOrderRepository;
import com.orderservice.usecase.OrderWriteUseCase;
import com.orderservice.usecase.dto.OrderDtoFromCart;
import com.orderservice.usecase.dto.OrderDtoFromProduct;
import com.orderservice.infrastructure.kafka.KafkaHealthIndicator;
import com.orderservice.infrastructure.kafka.OrderKafkaProducer;
import com.orderservice.infrastructure.kafka.event.ProductInfoEvent;
import com.orderservice.infrastructure.kafka.event.SubmitOrderEvent;
import com.orderservice.infrastructure.kafka.event.SubmitOrderEvents;
import com.orderservice.utils.error.ErrorCode;
import com.orderservice.utils.error.GlobalException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

class OrderWriteServiceTest extends IntegrationTestSupport {

    @Autowired
    private OrderWriteUseCase orderWriteUseCase;

    @Autowired
    private ProductOrderRepository productOrderRepository;
    @Autowired
    private OrderLineRepository orderLineRepository;
    @MockBean
    private ProductClient productClient;
    @MockBean
    private MemberClient memberClient;
    @MockBean
    private OrderKafkaProducer orderKafkaProducer;
    @MockBean
    private DeliveryClient deliveryClient;
    @MockBean
    private KafkaHealthIndicator kafkaHealthIndicator;

    @BeforeEach
    void setUp() {
        orderLineRepository.deleteAllInBatch();
        productOrderRepository.deleteAllInBatch();
    }

    @DisplayName("배송 중일 때 주문 취소 시 실패")
    @Test
    void in_delivery_cancel_order_fail() {
        // given
        long memberId = 1L;
        ProductOrder productOrder = ProductOrder.builder()
            .memberId(memberId)
            .productOrderStatus(ProductOrderStatus.COMPLETED)
            .totalPrice(2000L)
            .totalDiscount(0L)
            .build();
        productOrderRepository.save(productOrder);
        OrderLine orderLine = OrderLine.builder()
            .productId(1L)
            .productName("상품")
            .price(1000L)
            .orderLineStatus(OrderLineStatus.PAYMENT_COMPLETED)
            .discount(0L)
            .quantity(2L)
            .build();
        orderLine.assignToOrder(productOrder);
        productOrder.addOrderLine(orderLine);
        OrderLine saveOrderLine = orderLineRepository.save(orderLine);

        // when then
        when(deliveryClient.deliveryStatusCheck(saveOrderLine.getDeliveryId())).thenReturn(false);
        GlobalException result = assertThrows(GlobalException.class,
            () -> orderWriteUseCase.cancelOrder(memberId, saveOrderLine.getId()));
        assertEquals(result.getErrorCode(), ErrorCode.DELIVERY_STATUS_NOT_REQUESTED);
    }

    @DisplayName("주문 취소 성공")
    @Test
    void cancel_order() {
        // given
        long memberId = 1L;
        ProductOrder productOrder = ProductOrder.builder()
            .memberId(memberId)
            .productOrderStatus(ProductOrderStatus.COMPLETED)
            .totalPrice(2000L)
            .totalDiscount(0L)
            .build();
        productOrderRepository.save(productOrder);
        OrderLine orderLine = OrderLine.builder()
            .productId(1L)
            .productName("상품")
            .price(1000L)
            .orderLineStatus(OrderLineStatus.PAYMENT_COMPLETED)
            .discount(0L)
            .quantity(2L)
            .build();
        orderLine.assignToOrder(productOrder);
        productOrder.addOrderLine(orderLine);
        orderLineRepository.save(orderLine);

        // when
        when(deliveryClient.deliveryStatusCheck(any())).thenReturn(true);
        orderWriteUseCase.cancelOrder(memberId, productOrder.getId());
        OrderLine result = orderLineRepository.findAll().stream().findFirst().get();

        // then
        assertEquals(result.getOrderLineStatus(), OrderLineStatus.CANCELLED);
    }

    @DisplayName("장바구니에서 바로 주문 하기")
    @Test
    void submit_order_from_cart() throws JsonProcessingException {
        // given
        Long memberId = 1L;
        OrderDtoFromCart command = OrderDtoFromCart.builder()
            .cartIds(List.of(1L, 2L, 3L))
            .paymentMethodId(1L)
            .deliveryAddressId(1L)
            .build();
        List<CartDto> cartList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            CartDto cart = CartDto.builder()
                .productId(1L+i)
                .productName("상품"+i)
                .price(1000L)
                .thumbnailUrl("썸네일")
                .quantity(1L)
                .build();
            cartList.add(cart);
        }
        List<ProductInfoEvent> productInfoEvents = new ArrayList<>();
        for (CartDto cart : cartList) {
            ProductInfoEvent productInfo = ProductInfoEvent.builder()
                .productId(cart.productId())
                .quantity(cart.quantity())
                .productName(cart.productName())
                .price(cart.price())
                .thumbnailUrl(cart.thumbnailUrl())
                .build();
            productInfoEvents.add(productInfo);
        }
        SubmitOrderEvents submitOrderEvents = SubmitOrderEvents.builder()
            .memberId(memberId)
            .paymentMethodId(command.paymentMethodId())
            .deliveryAddressId(command.deliveryAddressId())
            .productInfo(productInfoEvents)
            .build();

        // when
        when(memberClient.getCartList(memberId, command.cartIds())).thenReturn(cartList);
        when(kafkaHealthIndicator.isKafkaUp()).thenReturn(true);
        orderWriteUseCase.submitOrderFromCart(memberId, command);

        // then
        verify(orderKafkaProducer, times(1)).occurSubmitOrderFromCartEvent(submitOrderEvents);

    }

    @DisplayName("상품에서 바로 주문 하기")
    @Test
    void submit_order_from_product() throws Exception {
        // given
        Long memberId = 1L;
        OrderDtoFromProduct command = OrderDtoFromProduct.builder()
            .paymentMethodId(1L)
            .deliveryAddressId(1L)
            .productId(1L)
            .quantity(3L)
            .build();
        ProductDto product = registeredProduct(command.productId(), 2000L);

        // when
        when(productClient.getProduct(product.productId())).thenReturn(product);
        when(kafkaHealthIndicator.isKafkaUp()).thenReturn(true);
        SubmitOrderEvent event = SubmitOrderEvent.builder()
            .memberId(memberId)
            .paymentMethodId(command.paymentMethodId())
            .deliveryAddressId(command.deliveryAddressId())
            .quantity(command.quantity())
            .productId(product.productId())
            .productName(product.productName())
            .price(product.price())
            .thumbnailUrl(product.thumbnailUrl())
            .build();
        orderWriteUseCase.submitOrderFromProduct(memberId, command);

        // then
        verify(orderKafkaProducer, times(1)).occurSubmitOrderFromProductEvent(event);
    }

    private static ProductDto registeredProduct(Long productId, Long price) {
        return new ProductDto(productId, "상품" + productId, price, "썸네일");
    }
}
