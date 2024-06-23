package com.ecommerce.delivery.usecase.impl;

import static org.junit.jupiter.api.Assertions.*;

import com.ecommerce.IntegrationTestSupport;
import com.ecommerce.common.AesUtil;
import com.ecommerce.delivery.controller.req.AddressRequestDto;
import com.ecommerce.delivery.controller.res.DeliveryAddressListResponseDto;
import com.ecommerce.delivery.entity.Delivery;
import com.ecommerce.delivery.entity.DeliveryAddress;
import com.ecommerce.delivery.repository.DeliveryAddressRepository;
import com.ecommerce.delivery.usecase.DeliveryAddressUseCase;
import com.ecommerce.delivery.usecase.DeliveryUseCase;
import com.ecommerce.member.entity.Member;
import com.ecommerce.member.repository.MemberRepository;
import com.ecommerce.order.entity.OrderLine;
import com.ecommerce.order.repository.OrderLineRepository;
import com.ecommerce.product.entity.Product;
import com.ecommerce.product.repository.ProductRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class DeliveryAddressServiceTest extends IntegrationTestSupport {

    @Autowired
    private DeliveryAddressUseCase deliveryAddressUseCase;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private DeliveryAddressRepository deliveryAddressRepository;
    @Autowired
    private DeliveryUseCase deliveryUseCase;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private OrderLineRepository orderLineRepository;

    @BeforeEach
    void setUp() {
        orderLineRepository.deleteAllInBatch();
        productRepository.deleteAllInBatch();
        deliveryAddressRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
    }

    @DisplayName("배송 요청")
    @Test
    void process_delivery() throws Exception {
        // given
        Member member = Member.builder().build();
        memberRepository.save(member);
        AddressRequestDto request = getAddressRequest(true);
        deliveryAddressUseCase.registerAddress(member.getId(), request);

        Product product = Product.builder().price(1000).build();
        productRepository.save(product);
        OrderLine orderLine = OrderLine.builder()
            .product(product)
            .quantity(3)
            .build();
        orderLineRepository.save(orderLine);
        DeliveryAddress findDeliveryAddress = deliveryAddressRepository.findAll().stream().findFirst()
            .get();

        // when
        Delivery result = deliveryUseCase.processDelivery(orderLine, findDeliveryAddress.getId());

        // then
        assertEquals(result.getDeliveryAddress().getId(), findDeliveryAddress.getId());
        assertEquals(result.getOrderLine().getId(), orderLine.getId());
        assertEquals(result.getProduct().getId(), product.getId());
    }

    @DisplayName("등록한 배송지 목록 조회")
    @Test
    void get_member_addresses() throws Exception {
        // given
        Member member = Member.builder().build();
        memberRepository.save(member);
        AddressRequestDto request = getAddressRequest(false);
        deliveryAddressUseCase.registerAddress(member.getId(),request);
        AddressRequestDto request2 = getAddressRequest(true);
        deliveryAddressUseCase.registerAddress(member.getId(),request2);

        // when
        DeliveryAddressListResponseDto result = deliveryAddressUseCase.getAddresses(
            member.getId());

        // then
        assertEquals(result.getDeliveryAddresses().size(), 2);
        assertEquals(result.getDeliveryAddresses().get(0).getStreet(), request2.getStreet());
        assertTrue(result.getDeliveryAddresses().get(0).isMainAddress());
    }

    @DisplayName("대표 배송지가 있는데 다시 대표 배송지로 등록할 경우")
    @Test
    void exist_main_address_retry_register_address() throws Exception {
        // given
        Member member = Member.builder().build();
        memberRepository.save(member);

        // when
        boolean isMainAddress = true;
        AddressRequestDto request = getAddressRequest(isMainAddress);
        deliveryAddressUseCase.registerAddress(member.getId(), request);
        deliveryAddressUseCase.registerAddress(member.getId(), request);

        // then
        List<DeliveryAddress> result = deliveryAddressRepository.findAllByMemberId(
            member.getId());
        assertEquals(result.stream().filter(DeliveryAddress::isMainAddress).count(), 1);
        assertFalse(result.get(0).isMainAddress());
        assertTrue(result.get(1).isMainAddress());
    }

    @DisplayName("배송지 등록")
    @Test
    void register_delivery_address() throws Exception {
        // given
        Member member = Member.builder().build();
        memberRepository.save(member);
        boolean isMainAddress = true;
        AddressRequestDto request = getAddressRequest(isMainAddress);

        // when
        deliveryAddressUseCase.registerAddress(member.getId(), request);
        DeliveryAddress deliveryAddress = deliveryAddressRepository.findAllByMemberId(member.getId())
            .stream().findFirst().orElse(null);
        System.out.println("deliveryAddress.getCreatedAt() : " + deliveryAddress.getCreatedAt());

        // then
        assertNotNull(deliveryAddress);
        assertEquals(deliveryAddress.isMainAddress(), request.isMainAddress());

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
}
