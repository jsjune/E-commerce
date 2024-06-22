package com.ecommerce.payment.usecase.impl;

import static org.junit.jupiter.api.Assertions.*;

import com.ecommerce.IntegrationTestSupport;
import com.ecommerce.member.entity.Member;
import com.ecommerce.member.repository.MemberRepository;
import com.ecommerce.order.entity.OrderLine;
import com.ecommerce.order.repository.OrderLineRepository;
import com.ecommerce.payment.entity.Payment;
import com.ecommerce.payment.entity.PaymentMethod;
import com.ecommerce.payment.entity.PaymentStatus;
import com.ecommerce.payment.repository.PaymentMethodRepository;
import com.ecommerce.payment.repository.PaymentRepository;
import com.ecommerce.payment.usecase.PaymentUseCase;
import com.ecommerce.product.entity.Product;
import com.ecommerce.product.repository.ProductRepository;
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
    private MemberRepository memberRepository;
    @Autowired
    private OrderLineRepository orderLineRepository;
    @Autowired
    private PaymentMethodRepository paymentMethodRepository;
    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        paymentMethodRepository.deleteAllInBatch();
        orderLineRepository.deleteAllInBatch();
        paymentRepository.deleteAllInBatch();
        productRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
    }

    @DisplayName("결제 진행 요청")
    @Test
    void process_payment() throws Exception {
        // given
        Member member = Member.builder().build();
        memberRepository.save(member);
        Product product = Product.builder().price(3000).build();
        productRepository.save(product);
        OrderLine orderLine = OrderLine.builder()
            .product(product)
            .quantity(2)
            .build();
        orderLineRepository.save(orderLine);
        PaymentMethod paymentMethod = PaymentMethod.builder().build();
        paymentMethodRepository.save(paymentMethod);

        // when
        paymentUseCase.processPayment(member, orderLine, paymentMethod.getId());
        Payment result = paymentRepository.findAll().stream().findFirst().get();

        // then
        assertEquals(result.getMember(), member);
        assertEquals(result.getOrderLine(), orderLine);
        assertEquals(result.getPaymentMethod(), paymentMethod);
        assertEquals(result.getStatus(), PaymentStatus.COMPLETED);
        assertEquals(result.getTotalPrice(), 6000);
    }
}
