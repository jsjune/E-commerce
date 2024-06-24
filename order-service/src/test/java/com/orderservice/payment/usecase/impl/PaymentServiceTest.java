package com.orderservice.payment.usecase.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.orderservice.IntegrationTestSupport;
import com.orderservice.adapter.dto.MemberDto;
import com.orderservice.adapter.dto.ProductDto;
import com.orderservice.order.entity.OrderLine;
import com.orderservice.order.repository.OrderLineRepository;
import com.orderservice.payment.entity.Payment;
import com.orderservice.payment.entity.PaymentMethod;
import com.orderservice.payment.entity.PaymentStatus;
import com.orderservice.payment.repository.PaymentMethodRepository;
import com.orderservice.payment.repository.PaymentRepository;
import com.orderservice.payment.usecase.PaymentUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class PaymentServiceTest extends IntegrationTestSupport {

    @Autowired
    private PaymentUseCase paymentUseCase;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private OrderLineRepository orderLineRepository;
    @Autowired
    private PaymentMethodRepository paymentMethodRepository;

    @BeforeEach
    void setUp() {
        paymentMethodRepository.deleteAllInBatch();
        orderLineRepository.deleteAllInBatch();
        paymentRepository.deleteAllInBatch();
    }

    @DisplayName("결제 진행 요청")
    @Test
    void process_payment() throws Exception {
        // given
        MemberDto member = new MemberDto(1L, "010-1234-5678", "회사");
        ProductDto product = registeredProduct(1L, 3000);
        OrderLine orderLine = OrderLine.builder()
            .productId(product.productId())
            .productName(product.productName())
            .price(product.price())
            .quantity(2)
            .build();
        orderLineRepository.save(orderLine);
        PaymentMethod paymentMethod = PaymentMethod.builder().build();
        paymentMethodRepository.save(paymentMethod);

        // when
        paymentUseCase.processPayment(member.memberId(), orderLine, paymentMethod.getId());
        Payment result = paymentRepository.findAll().stream().findFirst().get();

        // then
        assertEquals(result.getMemberId(), member.memberId());
        assertEquals(result.getOrderLine(), orderLine);
        assertEquals(result.getPaymentMethod(), paymentMethod);
        assertEquals(result.getStatus(), PaymentStatus.COMPLETED);
        assertEquals(result.getTotalPrice(), 6000);
    }

    private static ProductDto registeredProduct(Long productId, int price) {
        return new ProductDto(productId, "상품" + productId, price, "썸네일");
    }
}
