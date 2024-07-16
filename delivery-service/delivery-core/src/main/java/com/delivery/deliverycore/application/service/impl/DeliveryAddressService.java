package com.delivery.deliverycore.application.service.impl;


import com.delivery.deliverycore.infrastructure.entity.Address;
import com.delivery.deliverycore.infrastructure.entity.DeliveryAddress;
import com.delivery.deliveryapi.usecase.DeliveryAddressUseCase;
import com.delivery.deliveryapi.usecase.dto.DeliveryAddressListDto;
import com.delivery.deliveryapi.usecase.dto.DeliveryAddressListResponseDto;
import com.delivery.deliveryapi.usecase.dto.RegisterAddressDto;
import com.delivery.deliverycore.infrastructure.repository.DeliveryAddressRepository;
import com.delivery.deliverycore.application.utils.AesUtil;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class DeliveryAddressService implements DeliveryAddressUseCase {

    private final DeliveryAddressRepository deliveryAddressRepository;
    private final AesUtil aesUtil;

    @Override
    public void registerAddress(Long memberId, RegisterAddressDto command) throws Exception {
        deliveryAddressRepository.findByMemberIdAndMainAddress(memberId, true)
            .ifPresent(deliveryAddress -> deliveryAddress.assignMainAddress(false));
        Address address = Address.builder()
            .street(aesUtil.aesEncode(command.street()))
            .detailAddress(aesUtil.aesEncode(command.detailAddress()))
            .zipCode(aesUtil.aesEncode(command.zipCode()))
            .alias(command.alias())
            .build();
        DeliveryAddress deliveryAddress = DeliveryAddress.builder()
            .memberId(memberId)
            .address(address)
            .receiver(command.receiver())
            .mainAddress(command.mainAddress())
            .build();
        deliveryAddressRepository.save(deliveryAddress);
    }

    @Override
    public DeliveryAddressListResponseDto getAddresses(Long memberId) throws Exception {
        List<DeliveryAddress> deliveryAddresses = deliveryAddressRepository.findAllByMemberId(
                memberId)
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
                .mainAddress(deliveryAddress.isMainAddress())
                .build();
            DeliveryAddressList.add(addressListDto);
        }
        return new DeliveryAddressListResponseDto(DeliveryAddressList);
    }

}
