package com.order.ordercore.application.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.order.ordercore.application.service.OrderWriteUseCase;
import com.order.ordercore.application.service.dto.OrderDtoFromCart;
import com.order.ordercore.application.service.dto.OrderDtoFromProduct;
import com.order.ordercore.config.common.error.ErrorCode;
import com.order.ordercore.config.common.error.GlobalException;
import com.order.ordercore.testConfig.IntegrationTestSupport;
import com.order.ordercore.adapter.DeliveryClient;
import com.order.ordercore.adapter.MemberClient;
import com.order.ordercore.adapter.ProductClient;
import com.order.ordercore.application.service.dto.CartDto;
import com.order.ordercore.application.service.dto.ProductDto;
import com.order.ordercore.infrastructure.redis.RedisUtils;
import com.order.ordercore.infrastructure.entity.OrderLine;
import com.order.ordercore.infrastructure.entity.OrderLineStatus;
import com.order.ordercore.infrastructure.entity.ProductOrder;
import com.order.ordercore.infrastructure.entity.ProductOrderStatus;
import com.order.ordercore.infrastructure.kafka.event.SubmitOrderEvent;
import com.order.ordercore.infrastructure.kafka.event.SubmitOrderEvents;
import com.order.ordercore.infrastructure.repository.OrderLineRepository;
import com.order.ordercore.infrastructure.repository.ProductOrderRepository;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;

@RecordApplicationEvents
class OrderWriteServiceTest extends IntegrationTestSupport {

    @Autowired
    private ApplicationEvents events;
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
    private DeliveryClient deliveryClient;
    @MockBean
    private RedisUtils redisUtils;

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
        Assertions.assertEquals(result.getErrorCode(), ErrorCode.DELIVERY_STATUS_NOT_REQUESTED);
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
        Assertions.assertEquals(result.getOrderLineStatus(), OrderLineStatus.CANCELLED);
    }

    @DisplayName("장바구니에서 바로 주문 하기 - ")
    @Test
    void test() {
        // given

        // when

        // then

    }

    @DisplayName("장바구니에서 바로 주문 하기")
    @Test
    void submit_order_from_cart() {
        // given
        Long memberId = 1L;
        OrderDtoFromCart command = OrderDtoFromCart.builder()
            .cartIds(List.of(1L, 2L, 3L))
            .paymentMethodId(1L)
            .deliveryAddressId(1L)
            .build();
        List<CartDto> cartList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            cartList.add(CartDto.builder()
                .productId((long) i).build());
        }
        when(memberClient.getCartList(memberId, command.cartIds())).thenReturn(cartList);

        // when
        orderWriteUseCase.submitOrderFromCart(memberId, command);
        long count = events.stream(SubmitOrderEvents.class).count();

        // then
        assertEquals(count, 1);
    }

    @DisplayName("상품에서 바로 주문 하기")
    @Test
    void submit_order_from_product() {
        // given
        Long memberId = 1L;
        OrderDtoFromProduct command = OrderDtoFromProduct.builder()
            .paymentMethodId(1L)
            .deliveryAddressId(1L)
            .productId(1L)
            .quantity(3L)
            .build();
        ProductDto product = registeredProduct(command.productId(), 2000L);
        when(productClient.getProduct(product.productId())).thenReturn(product);
        when(redisUtils.decreaseStock(command.productId(), command.quantity())).thenReturn(true);

        // when
        orderWriteUseCase.submitOrderFromProduct(memberId, command);
        long count = events.stream(SubmitOrderEvent.class).count();

        // then
        assertEquals(count, 1);
    }

    private static ProductDto registeredProduct(Long productId, Long price) {
        return new ProductDto(productId, "상품" + productId, price, "썸네일");
    }
}
