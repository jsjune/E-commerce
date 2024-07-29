package com.order.ordercore.application.service.impl;

import static org.mockito.Mockito.doNothing;
import static reactor.core.publisher.Mono.when;

import com.order.ordercore.testConfig.IntegrationTestSupport;
import com.order.ordercore.application.service.dto.OrderRollbackDto;
import com.order.ordercore.infrastructure.redis.RedisUtils;
import com.order.ordercore.infrastructure.entity.OrderLine;
import com.order.ordercore.infrastructure.entity.OrderLineStatus;
import com.order.ordercore.infrastructure.entity.ProductOrder;
import com.order.ordercore.infrastructure.entity.ProductOrderStatus;
import com.order.ordercore.infrastructure.repository.OrderLineRepository;
import com.order.ordercore.infrastructure.repository.ProductOrderRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

class OrderRollbackServiceTest extends IntegrationTestSupport {

    @Autowired
    private OrderRollbackService orderRollbackService;
    @Autowired
    private ProductOrderRepository productOrderRepository;
    @Autowired
    private OrderLineRepository orderLineRepository;
    @MockBean
    private RedisUtils redisUtils;

    @BeforeEach
    void setUp() {
        orderLineRepository.deleteAllInBatch();
        productOrderRepository.deleteAllInBatch();
    }

    @DisplayName("주문 롤백")
    @Test
    void rollback_order() {
        // given
        ProductOrder productOrder = ProductOrder.builder()
            .totalPrice(10000L)
            .totalDiscount(0L)
            .productOrderStatus(ProductOrderStatus.COMPLETED)
            .build();
        productOrderRepository.save(productOrder);
        OrderLine orderLine = OrderLine.builder()
            .orderLineStatus(OrderLineStatus.PAYMENT_COMPLETED)
            .price(2000L)
            .quantity(5L)
            .paymentId(1L)
            .deliveryId(1L)
            .productOrder(productOrder)
            .build();
        productOrder.addOrderLine(orderLine);
        orderLineRepository.save(orderLine);
        OrderRollbackDto rollbackDto = OrderRollbackDto.builder()
            .productOrderId(productOrder.getId())
            .paymentId(orderLine.getPaymentId())
            .deliveryId(orderLine.getDeliveryId())
            .orderLineId(orderLine.getId())
            .totalPrice(productOrder.getTotalPrice())
            .totalDiscount(productOrder.getTotalDiscount())
            .build();
        doNothing().when(redisUtils).increaseStock(rollbackDto.productId(), rollbackDto.quantity());

        // when
        orderRollbackService.rollbackOrder(rollbackDto);
        ProductOrder result = productOrderRepository.findAll().stream().findFirst().get();

        // then
        Assertions.assertEquals(result.getTotalPrice(), 0);
        Assertions.assertEquals(result.getTotalDiscount(), 0);
        Assertions.assertEquals(result.getOrderLines().get(0).getOrderLineStatus(), OrderLineStatus.CANCELLED);
    }
}
