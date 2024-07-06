package com.orderservice.usecase.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import com.orderservice.IntegrationTestSupport;
import com.orderservice.adapter.MemberClient;
import com.orderservice.adapter.ProductClient;
import com.orderservice.adapter.res.CartDto;
import com.orderservice.adapter.res.MemberDto;
import com.orderservice.adapter.res.ProductDto;
import com.orderservice.controller.res.OrderDetailResponseDto;
import com.orderservice.controller.res.OrderListResponseDto;
import com.orderservice.entity.OrderLine;
import com.orderservice.entity.OrderLineStatus;
import com.orderservice.entity.ProductOrder;
import com.orderservice.entity.ProductOrderStatus;
import com.orderservice.repository.OrderLineRepository;
import com.orderservice.repository.ProductOrderRepository;
import com.orderservice.usecase.OrderReadUseCase;
import com.orderservice.usecase.dto.RegisterOrderFromCartDto;
import com.orderservice.usecase.dto.RegisterOrderFromProductDto;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

class OrderReadServiceTest extends IntegrationTestSupport {
    @Autowired
    private OrderReadUseCase orderReadUseCase;
    @Autowired
    private ProductOrderRepository productOrderRepository;
    @Autowired
    private OrderLineRepository orderLineRepository;
    @MockBean
    private ProductClient productClient;
    @MockBean
    private MemberClient memberClient;

    @BeforeEach
    void setUp() {
        orderLineRepository.deleteAllInBatch();
        productOrderRepository.deleteAllInBatch();
    }

    @DisplayName("상품 상세보기에서 주문서 작성")
    @Test
    void register_product_order_from_product() {
        // given
        MemberDto member = new MemberDto(1L, "010-1234-5678", "회사");
        ProductDto product = registeredProduct(1L, 2000L);
        Long quantity = 3L;
        RegisterOrderFromProductDto command = new RegisterOrderFromProductDto(product.productId(),
            quantity);

        // when
        when(productClient.getProduct(product.productId())).thenReturn(product);
        OrderDetailResponseDto result = orderReadUseCase.getOrderFromProduct(
            member.memberId(), command);

        // then
        assertEquals(result.getOrderLines().size(), 1);
        assertEquals(result.getOrderLines().get(0).getPrice(), product.price());
        assertEquals(result.getOrderLines().get(0).getQuantity(), quantity);
        assertEquals(result.getTotalPrice(), product.price() * quantity);
    }

    @DisplayName("장바구니에서 주문서 작성")
    @Test
    void register_product_order_from_cart() throws Exception {
        // given
        MemberDto member = new MemberDto(1L, "010-1234-5678", "회사");
        ProductDto product1 = registeredProduct(1L, 2000L);
        ProductDto product2 = registeredProduct(2L, 4000L);
        ;
        List<CartDto> carts = List.of(
            new CartDto(product1.productId(), product1.productName(), product1.price(), "썸네일", 1L),
            new CartDto(product2.productId(), product1.productName(), product2.price(), "썸네일", 2L)
        );
        when(memberClient.getCartList(member.memberId(), List.of(1L, 2L))).thenReturn(carts);
        RegisterOrderFromCartDto command = new RegisterOrderFromCartDto(
            List.of(product1.productId(), product2.productId()));

        // when
        OrderDetailResponseDto result = orderReadUseCase.getOrderFromCart(
            member.memberId(), command);

        // then
        assertEquals(result.getOrderLines().size(), 2);
        assertEquals(result.getOrderLines().get(0).getPrice(), product1.price());
        assertEquals(result.getOrderLines().get(0).getQuantity(), carts.get(0).quantity());
        assertEquals(result.getOrderLines().get(1).getPrice(), product2.price());
        assertEquals(result.getOrderLines().get(1).getQuantity(), carts.get(1).quantity());

    }

    @DisplayName("주문 목록 조회")
    @Test
    void get_product_orders() {
        // given
        Long memberId = 1L;
        ProductOrder productOrder = ProductOrder.builder()
            .memberId(memberId)
            .productOrderStatus(ProductOrderStatus.COMPLETED)
            .build();
        productOrderRepository.save(productOrder);
        for (int i = 0; i < 3; i++) {
            OrderLine orderLine = OrderLine.builder()
                .productOrder(productOrder)
                .orderLineStatus(OrderLineStatus.PAYMENT_COMPLETED)
                .build();
            productOrder.addOrderLine(orderLine);
            orderLineRepository.save(orderLine);
        }

        // when
        OrderListResponseDto result = orderReadUseCase.getOrders(memberId);

        // then
        assertEquals(result.getOrders().size(), 1);
        assertEquals(result.getOrders().get(0).getOrderLines().size(), 3);

    }

    @DisplayName("주문서 상세 조회")
    @Test
    void get_product_order() {
        // given
        long memberId = 1L;
        ProductOrder productOrder = ProductOrder.builder()
            .memberId(memberId)
            .productOrderStatus(ProductOrderStatus.COMPLETED)
            .totalPrice(14000L)
            .totalDiscount(0L)
            .build();
        productOrderRepository.save(productOrder);
        List<OrderLine> orderLines = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            OrderLine orderLine = OrderLine.builder()
                .productId((long) i)
                .productName("상품" + i)
                .price(1000L * (i + 1))
                .orderLineStatus(OrderLineStatus.PAYMENT_COMPLETED)
                .quantity(i + 1L)
                .build();
            orderLine.assignToOrder(productOrder);
            orderLines.add(orderLine);
            productOrder.addOrderLine(orderLine);
        }
        orderLineRepository.saveAll(orderLines);

        // when
        OrderDetailResponseDto result = orderReadUseCase.getOrder(memberId, productOrder.getId());

        // then
        assertEquals(result.getOrderLines().size(), 3);
        assertEquals(result.getTotalPrice(), 14000);
        assertEquals(result.getOrderLines().get(0).getPrice(), orderLines.get(0).getPrice());
        assertEquals(result.getOrderLines().get(1).getPrice(), orderLines.get(1).getPrice());
    }

    private static ProductDto registeredProduct(Long productId, Long price) {
        return new ProductDto(productId, "상품" + productId, price, "썸네일");
    }
}
