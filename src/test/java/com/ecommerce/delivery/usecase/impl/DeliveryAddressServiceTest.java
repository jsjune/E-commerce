package com.ecommerce.delivery.usecase.impl;

import static org.junit.jupiter.api.Assertions.*;

import com.ecommerce.IntegrationTestSupport;
import com.ecommerce.common.AesUtil;
import com.ecommerce.delivery.controller.req.AddressRequestDto;
import com.ecommerce.delivery.controller.res.MemberAddressListResponseDto;
import com.ecommerce.delivery.entity.MemberAddress;
import com.ecommerce.delivery.repository.MemberAddressRepository;
import com.ecommerce.delivery.usecase.DeliveryAddressUseCase;
import com.ecommerce.member.entity.Member;
import com.ecommerce.member.repository.MemberRepository;
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
    private MemberAddressRepository memberAddressRepository;
    @Autowired
    private AesUtil aesUtil;

    @BeforeEach
    void setUp() {
        memberAddressRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
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
        MemberAddressListResponseDto result = deliveryAddressUseCase.getAddresses(
            member.getId());

        // then
        assertEquals(result.getMemberAddresses().size(), 2);
        assertEquals(result.getMemberAddresses().get(0).getStreet(), request2.getStreet());
        assertTrue(result.getMemberAddresses().get(0).isMainAddress());
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
        List<MemberAddress> result = memberAddressRepository.findAllByMemberId(
            member.getId());
        assertEquals(result.stream().filter(MemberAddress::isMainAddress).count(), 1);
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
        MemberAddress memberAddress = memberAddressRepository.findAllByMemberId(member.getId())
            .stream().findFirst().orElse(null);

        // then
        assertNotNull(memberAddress);
        assertEquals(memberAddress.isMainAddress(), request.isMainAddress());

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
