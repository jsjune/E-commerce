package com.orderservice.usecase.impl;

import static org.junit.jupiter.api.Assertions.*;

import com.orderservice.IntegrationTestSupport;
import com.orderservice.infrastructure.entity.OrderLine;
import com.orderservice.infrastructure.entity.OrderLineStatus;
import com.orderservice.infrastructure.entity.ProductOrder;
import com.orderservice.infrastructure.entity.ProductOrderStatus;
import com.orderservice.infrastructure.repository.OrderLineRepository;
import com.orderservice.infrastructure.repository.ProductOrderRepository;
import com.orderservice.usecase.dto.OrderRollbackDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class OrderRollbackServiceTest extends IntegrationTestSupport {

    @Autowired
    private OrderRollbackService orderRollbackService;
    @Autowired
    private ProductOrderRepository productOrderRepository;
    @Autowired
    private OrderLineRepository orderLineRepository;

    @BeforeEach
    void setUp() {
        orderLineRepository.deleteAllInBatch();
        productOrderRepository.deleteAllInBatch();
    }

    @DisplayName("주문 롤백")
    @Test
    void test() {
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

        // when
        orderRollbackService.rollbackOrder(rollbackDto);
        ProductOrder result = productOrderRepository.findAll().stream().findFirst().get();

        // then
        assertEquals(result.getTotalPrice(), 0);
        assertEquals(result.getTotalDiscount(), 0);
        assertEquals(result.getOrderLines().get(0).getOrderLineStatus(), OrderLineStatus.CANCELLED);
    }
}
