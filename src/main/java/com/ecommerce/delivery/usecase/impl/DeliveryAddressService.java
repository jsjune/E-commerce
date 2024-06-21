package com.ecommerce.delivery.usecase.impl;

import com.ecommerce.common.AesUtil;
import com.ecommerce.delivery.controller.req.AddressRequestDto;
import com.ecommerce.delivery.controller.res.MemberAddressListDto;
import com.ecommerce.delivery.controller.res.MemberAddressListResponseDto;
import com.ecommerce.delivery.entity.Address;
import com.ecommerce.delivery.entity.MemberAddress;
import com.ecommerce.delivery.repository.MemberAddressRepository;
import com.ecommerce.delivery.usecase.DeliveryAddressUseCase;
import com.ecommerce.member.entity.Member;
import com.ecommerce.member.usecase.AuthUseCase;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class DeliveryAddressService implements DeliveryAddressUseCase {

    private final AuthUseCase authUseCase;
    private final MemberAddressRepository memberAddressRepository;
    private final AesUtil aesUtil;

    @Override
    public void registerAddress(Long memberId, AddressRequestDto request) throws Exception {
        Optional<Member> findMember = authUseCase.findById(memberId);
        if (findMember.isPresent()) {
            Member member = findMember.get();
            memberAddressRepository.findByMemberIdAndIsMainAddress(memberId, true)
                .ifPresent(memberAddress -> memberAddress.setMainAddress(false));
            Address address = Address.builder()
                .street(aesUtil.aesEncode(request.getStreet()))
                .detailAddress(aesUtil.aesEncode(request.getDetailAddress()))
                .zipCode(aesUtil.aesEncode(request.getZipCode()))
                .alias(request.getAlias())
                .build();
            MemberAddress memberAddress = MemberAddress.builder()
                .member(member)
                .address(address)
                .isMainAddress(request.isMainAddress())
                .build();
            memberAddressRepository.save(memberAddress);
        }
    }

    @Override
    public MemberAddressListResponseDto getAddresses(Long memberId) throws Exception {
        List<MemberAddress> memberAddresses = memberAddressRepository.findAllByMemberId(memberId)
            .stream()
            .sorted(Comparator.comparing(MemberAddress::isMainAddress, Comparator.reverseOrder()))
            .toList();
        List<MemberAddressListDto> memberAddressList = new ArrayList<>();
        for (MemberAddress memberAddress : memberAddresses) {
            MemberAddressListDto addressListDto = MemberAddressListDto.builder()
                .memberAddressId(memberAddress.getId())
                .street(aesUtil.aesDecode(memberAddress.getAddress().getStreet()))
                .detailAddress(aesUtil.aesDecode(memberAddress.getAddress().getDetailAddress()))
                .zipCode(aesUtil.aesDecode(memberAddress.getAddress().getZipCode()))
                .alias(memberAddress.getAddress().getAlias())
                .receiver(memberAddress.getReceiver())
                .isMainAddress(memberAddress.isMainAddress())
                .build();
            memberAddressList.add(addressListDto);
        }
        return new MemberAddressListResponseDto(memberAddressList);
    }

}
