//package com.orderservice.delivery.usecase.impl;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertFalse;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.when;
//
//import com.orderservice.IntegrationTestSupport;
//import com.orderservice.adapter.MemberClient;
//import com.orderservice.adapter.ProductClient;
//import com.orderservice.adapter.res.MemberDto;
//import com.orderservice.adapter.res.ProductDto;
//import com.orderservice.delivery.controller.req.AddressRequestDto;
//import com.orderservice.delivery.controller.res.DeliveryAddressListResponseDto;
//import com.orderservice.delivery.entity.Delivery;
//import com.orderservice.delivery.entity.DeliveryAddress;
//import com.orderservice.delivery.repository.DeliveryAddressRepository;
//import com.orderservice.delivery.usecase.DeliveryAddressUseCase;
//import com.orderservice.delivery.usecase.DeliveryUseCase;
//import com.orderservice.order.entity.OrderLine;
//import com.orderservice.order.repository.OrderLineRepository;
//import java.util.List;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.mock.mockito.MockBean;
//
//class DeliveryAddressServiceTest extends IntegrationTestSupport {
//
//    @Autowired
//    private DeliveryAddressUseCase deliveryAddressUseCase;
//    @Autowired
//    private DeliveryAddressRepository deliveryAddressRepository;
//    @Autowired
//    private DeliveryUseCase deliveryUseCase;
//    @Autowired
//    private OrderLineRepository orderLineRepository;
//    @MockBean
//    private ProductClient productClient;
//    @MockBean
//    private MemberClient memberClient;
//
//    @BeforeEach
//    void setUp() {
//        orderLineRepository.deleteAllInBatch();
//        deliveryAddressRepository.deleteAllInBatch();
//    }
//
//    @DisplayName("배송 요청")
//    @Test
//    void process_delivery() throws Exception {
//        // given
//        MemberDto member = new MemberDto(1L, "010-1234-5678", "회사");
//        when(memberClient.getMemberInfo(member.memberId())).thenReturn(member);
//
//        AddressRequestDto request = getAddressRequest(true);
//        deliveryAddressUseCase.registerAddress(member.memberId(), request);
//
//        ProductDto product = registeredProduct();
//        OrderLine orderLine = OrderLine.builder()
//            .productId(product.productId())
//            .productName(product.productName())
//            .price(product.price())
//            .thumbnailUrl(product.thumbnailUrl())
//            .quantity(3)
//            .build();
//        orderLineRepository.save(orderLine);
//        DeliveryAddress findDeliveryAddress = deliveryAddressRepository.findAll().stream().findFirst()
//            .get();
//
//        // when
//        when(productClient.getProduct(any())).thenReturn(product);
//        Delivery result = deliveryUseCase.processDelivery(orderLine, findDeliveryAddress.getId());
//
//        // then
//        assertEquals(result.getDeliveryAddress().getId(), findDeliveryAddress.getId());
//        assertEquals(result.getOrderLine().getId(), orderLine.getId());
//    }
//
//    @DisplayName("등록한 배송지 목록 조회")
//    @Test
//    void get_member_addresses() throws Exception {
//        // given
//        MemberDto member = new MemberDto(1L, "010-1234-5678", "회사");
//        when(memberClient.getMemberInfo(member.memberId())).thenReturn(member);
//        AddressRequestDto request = getAddressRequest(false);
//        deliveryAddressUseCase.registerAddress(member.memberId(),request);
//        AddressRequestDto request2 = getAddressRequest(true);
//        deliveryAddressUseCase.registerAddress(member.memberId(),request2);
//
//        // when
//        DeliveryAddressListResponseDto result = deliveryAddressUseCase.getAddresses(
//            member.memberId());
//
//        // then
//        assertEquals(result.getDeliveryAddresses().size(), 2);
//        assertEquals(result.getDeliveryAddresses().get(0).getStreet(), request2.getStreet());
//        assertTrue(result.getDeliveryAddresses().get(0).isMainAddress());
//    }
//
//    @DisplayName("대표 배송지가 있는데 다시 대표 배송지로 등록할 경우")
//    @Test
//    void exist_main_address_retry_register_address() throws Exception {
//        // given
//        MemberDto member = new MemberDto(1L, "010-1234-5678", "회사");
//        when(memberClient.getMemberInfo(member.memberId())).thenReturn(member);
//
//        // when
//        boolean isMainAddress = true;
//        AddressRequestDto request = getAddressRequest(isMainAddress);
//        deliveryAddressUseCase.registerAddress(member.memberId(), request);
//        deliveryAddressUseCase.registerAddress(member.memberId(), request);
//
//        // then
//        List<DeliveryAddress> result = deliveryAddressRepository.findAllByMemberId(
//            member.memberId());
//        assertEquals(result.stream().filter(DeliveryAddress::isMainAddress).count(), 1);
//        assertFalse(result.get(0).isMainAddress());
//        assertTrue(result.get(1).isMainAddress());
//    }
//
//    @DisplayName("배송지 등록")
//    @Test
//    void register_delivery_address() throws Exception {
//        // given
//        MemberDto member = new MemberDto(1L, "010-1234-5678", "회사");
//        when(memberClient.getMemberInfo(member.memberId())).thenReturn(member);
//        boolean isMainAddress = true;
//        AddressRequestDto request = getAddressRequest(isMainAddress);
//
//        // when
//        deliveryAddressUseCase.registerAddress(member.memberId(), request);
//        DeliveryAddress deliveryAddress = deliveryAddressRepository.findAllByMemberId(member.memberId())
//            .stream().findFirst().orElse(null);
//        System.out.println("deliveryAddress.getCreatedAt() : " + deliveryAddress.getCreatedAt());
//
//        // then
//        assertNotNull(deliveryAddress);
//        assertEquals(deliveryAddress.isMainAddress(), request.isMainAddress());
//
//    }
//
//    private static AddressRequestDto getAddressRequest(boolean isMainAddress) {
//        return AddressRequestDto.builder()
//            .street("서울시 강남구")
//            .detailAddress("역삼동")
//            .zipCode("12345")
//            .alias("집")
//            .receiver("홍길동")
//            .isMainAddress(isMainAddress)
//            .build();
//    }
//
//    private static ProductDto registeredProduct() {
//        return new ProductDto(1L, "상품", 1000, "썸네일");
//    }
//}
