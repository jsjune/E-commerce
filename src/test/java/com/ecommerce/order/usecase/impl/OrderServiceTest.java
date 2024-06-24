package com.ecommerce.order.usecase.impl;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.ecommerce.IntegrationTestSupport;
import com.ecommerce.common.adapter.ProductClient;
import com.ecommerce.common.adapter.dto.ProductDto;
import com.ecommerce.common.error.ErrorCode;
import com.ecommerce.common.error.GlobalException;
import com.ecommerce.delivery.controller.req.AddressRequestDto;
import com.ecommerce.delivery.entity.Delivery;
import com.ecommerce.delivery.entity.DeliveryAddress;
import com.ecommerce.delivery.entity.DeliveryStatus;
import com.ecommerce.delivery.repository.DeliveryAddressRepository;
import com.ecommerce.delivery.repository.DeliveryRepository;
import com.ecommerce.delivery.usecase.DeliveryAddressUseCase;
import com.ecommerce.member.entity.Cart;
import com.ecommerce.member.entity.Member;
import com.ecommerce.member.repository.CartRepository;
import com.ecommerce.member.repository.MemberRepository;
import com.ecommerce.member.usecase.CartUseCase;
import com.ecommerce.order.controller.req.CartOrderRequestDto;
import com.ecommerce.order.controller.req.OrderRequest;
import com.ecommerce.order.controller.req.ProductOrderRequestDto;
import com.ecommerce.order.controller.res.OrderDetailResponseDto;
import com.ecommerce.order.entity.OrderLine;
import com.ecommerce.order.entity.OrderLineStatus;
import com.ecommerce.order.entity.ProdcutOrderStatus;
import com.ecommerce.order.entity.ProductOrder;
import com.ecommerce.order.repository.OrderLineRepository;
import com.ecommerce.order.repository.ProductOrderRepository;
import com.ecommerce.order.usecase.OrderUseCase;
import com.ecommerce.payment.controller.req.PaymentMethodRequestDto;
import com.ecommerce.payment.entity.Payment;
import com.ecommerce.payment.entity.PaymentMethod;
import com.ecommerce.payment.entity.PaymentType;
import com.ecommerce.payment.repository.PaymentMethodRepository;
import com.ecommerce.payment.repository.PaymentRepository;
import com.ecommerce.payment.usecase.PaymentMethodUseCase;
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
    private MemberRepository memberRepository;
    @Autowired
    private ProductOrderRepository productOrderRepository;
    @Autowired
    private OrderLineRepository orderLineRepository;
    @Autowired
    private CartUseCase cartUseCase;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private PaymentMethodRepository paymentMethodRepository;
    @Autowired
    private DeliveryRepository deliveryRepository;
    @Autowired
    private DeliveryAddressRepository deliveryAddressRepository;
    @Autowired
    private PaymentMethodUseCase paymentMethodUseCase;
    @Autowired
    private DeliveryAddressUseCase deliveryAddressUseCase;
    @Autowired
    private CartRepository cartRepository;
    @MockBean
    private ProductClient productClient;

    @BeforeEach
    void setUp() {
        deliveryRepository.deleteAllInBatch();
        paymentRepository.deleteAllInBatch();
        orderLineRepository.deleteAllInBatch();
        productOrderRepository.deleteAllInBatch();
        deliveryAddressRepository.deleteAllInBatch();
        paymentMethodRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
    }

    @DisplayName("배송 중 주문 취소시 실패")
    @Test
    void in_delivery_order_cancel_fail() {
        // given
        OrderLine orderline = OrderLine.builder()
            .build();
        orderLineRepository.save(orderline);
        Delivery delivery = Delivery.builder()
            .orderLine(orderline)
            .status(DeliveryStatus.IN_DELIVERY)
            .build();
        deliveryRepository.save(delivery);
        orderline.finalizeOrderLine(null,null,delivery.getId());

        // when then
        GlobalException exception = assertThrows(GlobalException.class,
            () -> orderUseCase.cancelOrder(1L, orderline.getId()));
        assertEquals(exception.getErrorCode(), ErrorCode.DELIVERY_STATUS_NOT_REQUESTED);
    }

    @DisplayName("주문 취소 성공")
    @Test
    void cancel_order() throws Exception {
        // given
        Member member = Member.builder().build();
        memberRepository.save(member);

        PaymentMethodRequestDto paymentMethodRequest = getPaymentMethodRequestDto("bank");
        paymentMethodUseCase.registerPaymentMethod(member.getId(), paymentMethodRequest);
        PaymentMethod paymentMethod = paymentMethodRepository.findAll().stream().findFirst().get();

        boolean isMainAddress = true;
        AddressRequestDto deliveryRequest = getAddressRequest(isMainAddress);
        deliveryAddressUseCase.registerAddress(member.getId(), deliveryRequest);
        DeliveryAddress deliveryAddress = deliveryAddressRepository.findAllByMemberId(member.getId())
            .stream().findFirst().get();

        ProductDto product = registeredProduct(1L, 1000);
        when(productClient.getProduct(product.productId())).thenReturn(product);
        cartUseCase.addCart(member.getId(), product.productId());
        orderUseCase.registerOrderOfCart(member.getId(), List.of(product.productId()));
        ProductOrder productOrder = productOrderRepository.findAll().stream().findFirst().get();

        OrderRequest request = OrderRequest.builder()
            .orderId(productOrder.getId())
            .paymentMethodId(paymentMethod.getId())
            .deliveryAddressId(deliveryAddress.getId())
            .build();
        orderUseCase.submitOrder(member.getId(), request);

        // when
        orderUseCase.cancelOrder(member.getId(), productOrder.getOrderLines().get(0).getId());
        OrderLine orderLineResult = orderLineRepository.findById(
            productOrder.getOrderLines().get(0).getId()).get();

        // then
        assertEquals(orderLineResult.getOrderLineStatus(), OrderLineStatus.CANCELLED);
    }

    @DisplayName("주문서 상세 조회")
    @Test
    void get_product_order() throws Exception {
        // given
        Member member = Member.builder().build();
        memberRepository.save(member);

        PaymentMethodRequestDto paymentMethodRequest = getPaymentMethodRequestDto("bank");
        paymentMethodUseCase.registerPaymentMethod(member.getId(), paymentMethodRequest);
        PaymentMethod paymentMethod = paymentMethodRepository.findAll().stream().findFirst().get();

        boolean isMainAddress = true;
        AddressRequestDto deliveryRequest = getAddressRequest(isMainAddress);
        deliveryAddressUseCase.registerAddress(member.getId(), deliveryRequest);
        DeliveryAddress deliveryAddress = deliveryAddressRepository.findAllByMemberId(member.getId())
            .stream().findFirst().get();

        ProductDto product1 = registeredProduct(1L, 2000);
        ProductDto product2 = registeredProduct(2L, 4000);;
        when(productClient.getProduct(product1.productId())).thenReturn(product1);
        when(productClient.getProduct(product2.productId())).thenReturn(product2);
        cartUseCase.addCart(member.getId(), product1.productId());
        cartUseCase.addCart(member.getId(), product2.productId());
        cartUseCase.addCart(member.getId(), product2.productId());
        orderUseCase.registerOrderOfCart(member.getId(), List.of(product1.productId(), product2.productId()));
        ProductOrder productOrder = productOrderRepository.findAll().stream().findFirst().get();

        OrderRequest request = OrderRequest.builder()
            .orderId(productOrder.getId())
            .paymentMethodId(paymentMethod.getId())
            .deliveryAddressId(deliveryAddress.getId())
            .build();

        // when
        orderUseCase.submitOrder(member.getId(), request);
        OrderDetailResponseDto result = orderUseCase.getOrder(member.getId(), productOrder.getId());

        // then
        assertEquals(result.getOrderLines().size(), 2);
        assertEquals(result.getOrderLines().get(0).getPrice(), product1.price());
        assertEquals(result.getOrderLines().get(0).getQuantity(), 1);
        assertEquals(result.getOrderLines().get(1).getPrice(), product2.price());
        assertEquals(result.getOrderLines().get(1).getQuantity(), 2);
        assertEquals(result.getTotalPrice(), 10000);
    }

    @DisplayName("주문 하기")
    @Test
    void order() throws Exception {
        // given
        Member member = Member.builder().build();
        memberRepository.save(member);

        PaymentMethodRequestDto paymentMethodRequest = getPaymentMethodRequestDto("bank");
        paymentMethodUseCase.registerPaymentMethod(member.getId(), paymentMethodRequest);
        PaymentMethod paymentMethod = paymentMethodRepository.findAll().stream().findFirst().get();

        boolean isMainAddress = true;
        AddressRequestDto deliveryRequest = getAddressRequest(isMainAddress);
        deliveryAddressUseCase.registerAddress(member.getId(), deliveryRequest);
        DeliveryAddress deliveryAddress = deliveryAddressRepository.findAllByMemberId(member.getId())
            .stream().findFirst().get();

        ProductDto product1 = registeredProduct(1L, 2000);
        ProductDto product2 = registeredProduct(2L, 4000);;
        when(productClient.getProduct(product1.productId())).thenReturn(product1);
        when(productClient.getProduct(product2.productId())).thenReturn(product2);
        cartUseCase.addCart(member.getId(), product1.productId());
        cartUseCase.addCart(member.getId(), product2.productId());
        cartUseCase.addCart(member.getId(), product2.productId());
        orderUseCase.registerOrderOfCart(member.getId(), List.of(product1.productId(), product2.productId()));
        ProductOrder productOrder = productOrderRepository.findAll().stream().findFirst().get();

        OrderRequest request = OrderRequest.builder()
            .orderId(productOrder.getId())
            .paymentMethodId(paymentMethod.getId())
            .deliveryAddressId(deliveryAddress.getId())
            .build();

        // when

        orderUseCase.submitOrder(member.getId(), request);

        ProductOrder orderResult = productOrderRepository.findAll().stream().findFirst().get();
        List<Payment> paymentResult = paymentRepository.findAll();
        List<Delivery> deliveryResult = deliveryRepository.findAll();
        List<Cart> cartResult = cartRepository.findAll();

        // then
        assertEquals(deliveryResult.size(), 2);
        assertEquals(paymentResult.size(), 2);
        assertEquals(orderResult.getProductOrderStatus(), ProdcutOrderStatus.COMPLETED);
        assertEquals(orderResult.getTotalPrice(), paymentResult.stream().mapToInt(Payment::getTotalPrice).sum());
        assertEquals(cartResult.size(), 0);
    }

    @DisplayName("상품 상세보기에서 주문서 작성")
    @Test
    void register_product_order_from_product() {
        // given
        Member member = Member.builder().build();
        memberRepository.save(member);
        ProductDto product = registeredProduct(1L, 2000);
        ProductOrderRequestDto request = new ProductOrderRequestDto(product.productId(), 3);

        // when
        when(productClient.getProduct(product.productId())).thenReturn(product);
        orderUseCase.registerOrder(member.getId(), request);
        ProductOrder result = productOrderRepository.findAll().stream().findFirst()
            .orElse(null);

        // then
        assertEquals(result.getOrderLines().size(), 1);
        assertEquals(result.getOrderLines().get(0).getPrice(), product.price());
        assertEquals(result.getOrderLines().get(0).getQuantity(), 3);
    }
    
    @DisplayName("장바구니에서 주문서 작성")
    @Test
    void register_product_order_from_cart() {
        // given
        Member member = Member.builder().build();
        memberRepository.save(member);
        ProductDto product1 = registeredProduct(1L, 2000);
        ProductDto product2 = registeredProduct(2L, 4000);;
        when(productClient.getProduct(product1.productId())).thenReturn(product1);
        when(productClient.getProduct(product2.productId())).thenReturn(product2);
        cartUseCase.addCart(member.getId(), product1.productId());
        cartUseCase.addCart(member.getId(), product2.productId());
        cartUseCase.addCart(member.getId(), product2.productId());
        CartOrderRequestDto request = new CartOrderRequestDto(List.of(product1.productId(), product2.productId()));

        // when
        orderUseCase.registerOrderOfCart(member.getId(), request.getCartIds());
        ProductOrder result = productOrderRepository.findAll().stream().findFirst()
            .orElse(null);

        // then
        assertEquals(result.getMember().getId(), member.getId());
        assertEquals(result.getOrderLines().size(), 2);
        assertEquals(result.getOrderLines().get(0).getPrice(), product1.price());
        assertEquals(result.getOrderLines().get(1).getPrice(), product2.price());
        assertEquals(result.getOrderLines().get(0).getQuantity(), 1);
        assertEquals(result.getOrderLines().get(1).getQuantity(), 2);

    }

    private static AddressRequestDto getAddressRequest(boolean isMainAddress) {
        return AddressRequestDto.builder()
            .street("서울시 강남구")
            .detailAddress("역삼동")
            .zipCode("12345")
            .alias("집")
            .receiver("홍길동")
            .isMainAddress(isMainAddress)
            .build();
    }

    private static PaymentMethodRequestDto getPaymentMethodRequestDto(String bank) {
        return PaymentMethodRequestDto.builder()
            .paymentType(PaymentType.CREDIT_CARD)
            .creditCardNumber("1234-1234-1234-1234")
            .bank(bank)
            .accountNumber(null)
            .build();
    }

    private static ProductDto registeredProduct(Long productId, int price) {
        return new ProductDto(productId, "상품" + productId, price, "썸네일");
    }
}
