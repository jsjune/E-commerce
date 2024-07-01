package com.orderservice.order.usecase.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.orderservice.IntegrationTestSupport;
import com.orderservice.adapter.DeliveryClient;
import com.orderservice.adapter.MemberClient;
import com.orderservice.adapter.PaymentClient;
import com.orderservice.adapter.ProductClient;
import com.orderservice.adapter.res.CartDto;
import com.orderservice.adapter.res.MemberDto;
import com.orderservice.adapter.res.PaymentDto;
import com.orderservice.adapter.res.ProductDto;
import com.orderservice.controller.res.OrderDetailResponseDto;
import com.orderservice.controller.res.OrderListResponseDto;
import com.orderservice.entity.OrderLine;
import com.orderservice.entity.OrderLineStatus;
import com.orderservice.entity.ProductOrderStatus;
import com.orderservice.entity.ProductOrder;
import com.orderservice.repository.OrderLineRepository;
import com.orderservice.repository.ProductOrderRepository;
import com.orderservice.usecase.OrderUseCase;
import com.orderservice.usecase.dto.OrderDto;
import com.orderservice.usecase.dto.RegisterOrderOfCartDto;
import com.orderservice.usecase.dto.RegisterOrderOfProductDto;
import com.orderservice.utils.error.ErrorCode;
import com.orderservice.utils.error.GlobalException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

class OrderServiceTest extends IntegrationTestSupport {

    @Autowired
    private OrderUseCase orderUseCase;
    @Autowired
    private ProductOrderRepository productOrderRepository;
    @Autowired
    private OrderLineRepository orderLineRepository;
    @MockBean
    private ProductClient productClient;
    @MockBean
    private MemberClient memberClient;
    @MockBean
    private PaymentClient paymentClient;
    @MockBean
    private DeliveryClient deliveryClient;

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
            () -> orderUseCase.cancelOrder(memberId, saveOrderLine.getId()));
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
        orderUseCase.cancelOrder(memberId, productOrder.getId());
        OrderLine result = orderLineRepository.findAll().stream().findFirst().get();

        // then
        assertEquals(result.getOrderLineStatus(),OrderLineStatus.CANCELLED);
    }

    @DisplayName("주문 목록 조회")
    @Test
    void test() {
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
        OrderListResponseDto result = orderUseCase.getOrders(memberId);

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
        OrderDetailResponseDto result = orderUseCase.getOrder(memberId, productOrder.getId());

        // then
        assertEquals(result.getProductOrderId(), productOrder.getId());
        assertEquals(result.getOrderLines().size(), 3);
        assertEquals(result.getTotalPrice(), 14000);
        assertEquals(result.getOrderLines().get(0).getPrice(), 1000);
        assertEquals(result.getOrderLines().get(1).getPrice(), 2000);
    }

    @DisplayName("주문 하기")
    @Test
    void order() throws Exception {
        // given
        MemberDto member = new MemberDto(1L, "010-1234-5678", "회사");
        ProductDto product = registeredProduct(1L, 2000L);
        Long quantity = 3L;
        RegisterOrderOfProductDto registerCommand = new RegisterOrderOfProductDto(product.productId(),
            quantity);

        when(productClient.getProduct(product.productId())).thenReturn(product);
        orderUseCase.registerOrder(member.memberId(), registerCommand);
        ProductOrder productOrder = productOrderRepository.findAll().stream().findFirst()
            .orElse(null);

        // when
        OrderDto orderCommand = OrderDto.builder()
            .orderId(productOrder.getId())
            .paymentMethodId(1L)
            .deliveryAddressId(1L)
            .build();
        when(paymentClient.processPayment(any())).thenReturn(new PaymentDto(1L, product.price() * quantity, 0L));
        when(deliveryClient.processDelivery(any())).thenReturn(1L);
        when(productClient.decreaseStock(product.productId(), quantity)).thenReturn(true);
        orderUseCase.submitOrder(member.memberId(), orderCommand);
        ProductOrder result = productOrderRepository.findById(productOrder.getId()).get();

        // then
        assertEquals(result.getProductOrderStatus(), ProductOrderStatus.COMPLETED);
        assertEquals(result.getTotalPrice(), 6000);

    }

    @DisplayName("상품 상세보기에서 주문서 작성")
    @Test
    void register_product_order_from_product() {
        // given
        MemberDto member = new MemberDto(1L, "010-1234-5678", "회사");
        ProductDto product = registeredProduct(1L, 2000L);
        Long quantity = 3L;
        RegisterOrderOfProductDto command = new RegisterOrderOfProductDto(product.productId(), quantity);

        // when
        when(productClient.getProduct(product.productId())).thenReturn(product);
        orderUseCase.registerOrder(member.memberId(), command);
        ProductOrder result = productOrderRepository.findAll().stream().findFirst()
            .orElse(null);

        // then
        assertEquals(result.getOrderLines().size(), 1);
        assertEquals(result.getOrderLines().get(0).getPrice(), product.price());
        assertEquals(result.getOrderLines().get(0).getQuantity(), quantity);
        assertEquals(result.getTotalPrice(), 6000);
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
        RegisterOrderOfCartDto command = new RegisterOrderOfCartDto(
            List.of(product1.productId(), product2.productId()));

        // when
        orderUseCase.registerOrderOfCart(member.memberId(), command);
        ProductOrder result = productOrderRepository.findAll().stream().findFirst()
            .orElse(null);

        // then
        assertEquals(result.getMemberId(), member.memberId());
        assertEquals(result.getOrderLines().size(), 2);
        assertEquals(result.getOrderLines().get(0).getPrice(), product1.price());
        assertEquals(result.getOrderLines().get(0).getQuantity(), 1);
        assertEquals(result.getOrderLines().get(1).getPrice(), product2.price());
        assertEquals(result.getOrderLines().get(1).getQuantity(), 2);

    }

    private static ProductDto registeredProduct(Long productId, Long price) {
        return new ProductDto(productId, "상품" + productId, price, "썸네일");
    }
}
