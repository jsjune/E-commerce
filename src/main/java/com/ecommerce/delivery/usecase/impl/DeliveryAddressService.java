package com.ecommerce.delivery.usecase.impl;

import com.ecommerce.common.AesUtil;
import com.ecommerce.delivery.controller.req.AddressRequestDto;
import com.ecommerce.delivery.controller.res.DeliveryAddressListDto;
import com.ecommerce.delivery.controller.res.DeliveryAddressListResponseDto;
import com.ecommerce.delivery.entity.Address;
import com.ecommerce.delivery.entity.DeliveryAddress;
import com.ecommerce.delivery.repository.DeliveryAddressRepository;
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
    private final DeliveryAddressRepository deliveryAddressRepository;
    private final AesUtil aesUtil;

    @Override
    public void registerAddress(Long memberId, AddressRequestDto request) throws Exception {
        Optional<Member> findMember = authUseCase.findById(memberId);
        if (findMember.isPresent()) {
            Member member = findMember.get();
            deliveryAddressRepository.findByMemberIdAndIsMainAddress(memberId, true)
                .ifPresent(deliveryAddress -> deliveryAddress.setMainAddress(false));
            Address address = Address.builder()
                .street(aesUtil.aesEncode(request.getStreet()))
                .detailAddress(aesUtil.aesEncode(request.getDetailAddress()))
                .zipCode(aesUtil.aesEncode(request.getZipCode()))
                .alias(request.getAlias())
                .build();
            DeliveryAddress deliveryAddress = DeliveryAddress.builder()
                .member(member)
                .address(address)
                .receiver(request.getReceiver())
                .isMainAddress(request.isMainAddress())
                .build();
            deliveryAddressRepository.save(deliveryAddress);
        }
    }

    @Override
    public DeliveryAddressListResponseDto getAddresses(Long memberId) throws Exception {
        List<DeliveryAddress> deliveryAddresses = deliveryAddressRepository.findAllByMemberId(memberId)
            .stream()
            .sorted(Comparator.comparing(DeliveryAddress::isMainAddress, Comparator.reverseOrder()))
            .toList();
        List<DeliveryAddressListDto> DeliveryAddressList = new ArrayList<>();
        for (DeliveryAddress deliveryAddress : deliveryAddresses) {
            DeliveryAddressListDto addressListDto = DeliveryAddressListDto.builder()
                .deliveryAddressId(deliveryAddress.getId())
                .street(aesUtil.aesDecode(deliveryAddress.getAddress().getStreet()))
                .detailAddress(aesUtil.aesDecode(deliveryAddress.getAddress().getDetailAddress()))
                .zipCode(aesUtil.aesDecode(deliveryAddress.getAddress().getZipCode()))
                .alias(deliveryAddress.getAddress().getAlias())
                .receiver(deliveryAddress.getReceiver())
                .isMainAddress(deliveryAddress.isMainAddress())
                .build();
            DeliveryAddressList.add(addressListDto);
        }
        return new DeliveryAddressListResponseDto(DeliveryAddressList);
    }

}
