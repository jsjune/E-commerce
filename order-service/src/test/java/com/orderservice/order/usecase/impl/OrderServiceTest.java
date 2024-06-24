package com.orderservice.order.usecase.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import com.orderservice.IntegrationTestSupport;
import com.orderservice.adapter.MemberClient;
import com.orderservice.adapter.ProductClient;
import com.orderservice.adapter.dto.CartDto;
import com.orderservice.adapter.dto.MemberDto;
import com.orderservice.adapter.dto.ProductDto;
import com.orderservice.delivery.controller.req.AddressRequestDto;
import com.orderservice.delivery.entity.Delivery;
import com.orderservice.delivery.entity.DeliveryAddress;
import com.orderservice.delivery.entity.DeliveryStatus;
import com.orderservice.delivery.repository.DeliveryAddressRepository;
import com.orderservice.delivery.repository.DeliveryRepository;
import com.orderservice.delivery.usecase.DeliveryAddressUseCase;
import com.orderservice.order.controller.req.CartOrderRequestDto;
import com.orderservice.order.controller.req.OrderRequest;
import com.orderservice.order.controller.req.ProductOrderRequestDto;
import com.orderservice.order.controller.res.OrderDetailResponseDto;
import com.orderservice.order.entity.OrderLine;
import com.orderservice.order.entity.OrderLineStatus;
import com.orderservice.order.entity.ProdcutOrderStatus;
import com.orderservice.order.entity.ProductOrder;
import com.orderservice.order.repository.OrderLineRepository;
import com.orderservice.order.repository.ProductOrderRepository;
import com.orderservice.order.usecase.OrderUseCase;
import com.orderservice.payment.controller.req.PaymentMethodRequestDto;
import com.orderservice.payment.entity.Payment;
import com.orderservice.payment.entity.PaymentMethod;
import com.orderservice.payment.entity.PaymentType;
import com.orderservice.payment.repository.PaymentMethodRepository;
import com.orderservice.payment.repository.PaymentRepository;
import com.orderservice.payment.usecase.PaymentMethodUseCase;
import com.orderservice.utils.error.ErrorCode;
import com.orderservice.utils.error.GlobalException;
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
    @MockBean
    private ProductClient productClient;
    @MockBean
    private MemberClient memberClient;

    @BeforeEach
    void setUp() {
        deliveryRepository.deleteAllInBatch();
        paymentRepository.deleteAllInBatch();
        orderLineRepository.deleteAllInBatch();
        productOrderRepository.deleteAllInBatch();
        deliveryAddressRepository.deleteAllInBatch();
        paymentMethodRepository.deleteAllInBatch();
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
        MemberDto member = new MemberDto(1L, "010-1234-5678", "회사");
        when(memberClient.getMemberInfo(member.memberId())).thenReturn(member);

        PaymentMethodRequestDto paymentMethodRequest = getPaymentMethodRequestDto("bank");
        paymentMethodUseCase.registerPaymentMethod(member.memberId(), paymentMethodRequest);
        PaymentMethod paymentMethod = paymentMethodRepository.findAll().stream().findFirst().get();

        boolean isMainAddress = true;
        AddressRequestDto deliveryRequest = getAddressRequest(isMainAddress);
        deliveryAddressUseCase.registerAddress(member.memberId(), deliveryRequest);
        DeliveryAddress deliveryAddress = deliveryAddressRepository.findAllByMemberId(member.memberId())
            .stream().findFirst().get();

        ProductDto product = registeredProduct(1L, 1000);
        when(productClient.getProduct(product.productId())).thenReturn(product);
        List<CartDto> carts = List.of(
            new CartDto(product.productId(), product.productName(), product.price(), "썸네일",3)
        );
        when(memberClient.getCartList(member.memberId(), List.of(1L))).thenReturn(carts);
        orderUseCase.registerOrderOfCart(member.memberId(), List.of(product.productId()));
        ProductOrder productOrder = productOrderRepository.findById(product.productId()).get();

        OrderRequest request = OrderRequest.builder()
            .orderId(productOrder.getId())
            .paymentMethodId(paymentMethod.getId())
            .deliveryAddressId(deliveryAddress.getId())
            .build();
        orderUseCase.submitOrder(member.memberId(), request);

        // when
        orderUseCase.cancelOrder(member.memberId(), productOrder.getOrderLines().get(0).getId());
        OrderLine orderLineResult = orderLineRepository.findById(
            productOrder.getOrderLines().get(0).getId()).get();

        // then
        assertEquals(orderLineResult.getOrderLineStatus(), OrderLineStatus.CANCELLED);
    }

    @DisplayName("주문서 상세 조회")
    @Test
    void get_product_order() throws Exception {
        // given
        MemberDto member = new MemberDto(1L, "010-1234-5678", "회사");

        PaymentMethodRequestDto paymentMethodRequest = getPaymentMethodRequestDto("bank");
        paymentMethodUseCase.registerPaymentMethod(member.memberId(), paymentMethodRequest);
        PaymentMethod paymentMethod = paymentMethodRepository.findAll().stream().findFirst().get();

        boolean isMainAddress = true;
        AddressRequestDto deliveryRequest = getAddressRequest(isMainAddress);
        when(memberClient.getMemberInfo(member.memberId())).thenReturn(member);
        deliveryAddressUseCase.registerAddress(member.memberId(), deliveryRequest);
        DeliveryAddress deliveryAddress = deliveryAddressRepository.findAllByMemberId(member.memberId())
            .stream().findFirst().get();

        ProductDto product1 = registeredProduct(1L, 2000);
        ProductDto product2 = registeredProduct(2L, 4000);;
        when(productClient.getProduct(product1.productId())).thenReturn(product1);
        when(productClient.getProduct(product2.productId())).thenReturn(product2);
        List<CartDto> carts = List.of(
            new CartDto(product1.productId(), product1.productName(), product1.price(), "썸네일",3),
            new CartDto(product2.productId(), product1.productName(), product2.price(), "썸네일",2)
        );
        when(memberClient.getCartList(member.memberId(), List.of(1L,2L))).thenReturn(carts);
        orderUseCase.registerOrderOfCart(member.memberId(), List.of(product1.productId(), product2.productId()));
        ProductOrder productOrder = productOrderRepository.findAll().stream().findFirst().get();

        OrderRequest request = OrderRequest.builder()
            .orderId(productOrder.getId())
            .paymentMethodId(paymentMethod.getId())
            .deliveryAddressId(deliveryAddress.getId())
            .build();

        // when
        orderUseCase.submitOrder(member.memberId(), request);
        OrderDetailResponseDto result = orderUseCase.getOrder(member.memberId(), productOrder.getId());

        // then
        assertEquals(result.getOrderLines().size(), 2);
        assertEquals(result.getOrderLines().get(0).getPrice(), product1.price());
        assertEquals(result.getOrderLines().get(0).getQuantity(), 3);
        assertEquals(result.getOrderLines().get(1).getPrice(), product2.price());
        assertEquals(result.getOrderLines().get(1).getQuantity(), 2);
        assertEquals(result.getTotalPrice(), 14000);
    }

    @DisplayName("주문 하기")
    @Test
    void order() throws Exception {
        // given
        MemberDto member = new MemberDto(1L, "010-1234-5678", "회사");
        when(memberClient.getMemberInfo(member.memberId())).thenReturn(member);

        PaymentMethodRequestDto paymentMethodRequest = getPaymentMethodRequestDto("bank");
        paymentMethodUseCase.registerPaymentMethod(member.memberId(), paymentMethodRequest);
        PaymentMethod paymentMethod = paymentMethodRepository.findAll().stream().findFirst().get();

        boolean isMainAddress = true;
        AddressRequestDto deliveryRequest = getAddressRequest(isMainAddress);
        deliveryAddressUseCase.registerAddress(member.memberId(), deliveryRequest);
        DeliveryAddress deliveryAddress = deliveryAddressRepository.findAllByMemberId(member.memberId())
            .stream().findFirst().get();

        ProductDto product1 = registeredProduct(1L, 2000);
        ProductDto product2 = registeredProduct(2L, 4000);;
        when(productClient.getProduct(product1.productId())).thenReturn(product1);
        when(productClient.getProduct(product2.productId())).thenReturn(product2);
        List<CartDto> carts = List.of(
            new CartDto(product1.productId(), product1.productName(), product1.price(), "썸네일",3),
            new CartDto(product2.productId(), product1.productName(), product2.price(), "썸네일",2)
        );
        when(memberClient.getCartList(member.memberId(), List.of(1L,2L))).thenReturn(carts);
        orderUseCase.registerOrderOfCart(member.memberId(), List.of(product1.productId(), product2.productId()));
        ProductOrder productOrder = productOrderRepository.findAll().stream().findFirst().get();

        OrderRequest request = OrderRequest.builder()
            .orderId(productOrder.getId())
            .paymentMethodId(paymentMethod.getId())
            .deliveryAddressId(deliveryAddress.getId())
            .build();

        // when
        doNothing().when(productClient).decreaseStock(product1.productId(), 3);
        doNothing().when(productClient).decreaseStock(product2.productId(), 2);
        when(productClient.getProduct(product2.productId())).thenReturn(product2);
        orderUseCase.submitOrder(member.memberId(), request);

        ProductOrder orderResult = productOrderRepository.findAll().stream().findFirst().get();
        List<Payment> paymentResult = paymentRepository.findAll();
        List<Delivery> deliveryResult = deliveryRepository.findAll();

        // then
        assertEquals(deliveryResult.size(), 2);
        assertEquals(paymentResult.size(), 2);
        assertEquals(orderResult.getProductOrderStatus(), ProdcutOrderStatus.COMPLETED);
        assertEquals(orderResult.getTotalPrice(), paymentResult.stream().mapToInt(Payment::getTotalPrice).sum());
    }

    @DisplayName("상품 상세보기에서 주문서 작성")
    @Test
    void register_product_order_from_product() {
        // given
        MemberDto member = new MemberDto(1L, "010-1234-5678", "회사");
        ProductDto product = registeredProduct(1L, 2000);
        ProductOrderRequestDto request = new ProductOrderRequestDto(product.productId(), 3);

        // when
        when(productClient.getProduct(product.productId())).thenReturn(product);
        orderUseCase.registerOrder(member.memberId(), request);
        ProductOrder result = productOrderRepository.findAll().stream().findFirst()
            .orElse(null);

        // then
        assertEquals(result.getOrderLines().size(), 1);
        assertEquals(result.getOrderLines().get(0).getPrice(), product.price());
        assertEquals(result.getOrderLines().get(0).getQuantity(), 3);
    }
    
    @DisplayName("장바구니에서 주문서 작성")
    @Test
    void register_product_order_from_cart() throws Exception {
        // given
        MemberDto member = new MemberDto(1L, "010-1234-5678", "회사");
        ProductDto product1 = registeredProduct(1L, 2000);
        ProductDto product2 = registeredProduct(2L, 4000);;
        List<CartDto> carts = List.of(
            new CartDto(product1.productId(), product1.productName(), product1.price(), "썸네일",1),
            new CartDto(product2.productId(), product1.productName(), product2.price(), "썸네일",2)
        );
        when(memberClient.getCartList(member.memberId(),List.of(1L,2L))).thenReturn(carts);
        CartOrderRequestDto request = new CartOrderRequestDto(List.of(product1.productId(), product2.productId()));

        // when
        orderUseCase.registerOrderOfCart(member.memberId(), request.getCartIds());
        ProductOrder result = productOrderRepository.findAll().stream().findFirst()
            .orElse(null);

        // then
        assertEquals(result.getMemberId(), member.memberId());
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
