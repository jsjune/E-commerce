package com.deliveryservice.usecase.impl;


import com.deliveryservice.controller.res.DeliveryAddressListDto;
import com.deliveryservice.controller.res.DeliveryAddressListResponseDto;
import com.deliveryservice.entity.Address;
import com.deliveryservice.entity.DeliveryAddress;
import com.deliveryservice.repository.DeliveryAddressRepository;
import com.deliveryservice.usecase.DeliveryAddressUseCase;
import com.deliveryservice.usecase.dto.RegisterAddress;
import com.deliveryservice.utils.AesUtil;
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
    public void registerAddress(Long memberId, RegisterAddress command) throws Exception {
        deliveryAddressRepository.findByMemberIdAndIsMainAddress(memberId, true)
            .ifPresent(deliveryAddress -> deliveryAddress.setMainAddress(false));
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
            .isMainAddress(command.isMainAddress())
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
                .isMainAddress(deliveryAddress.isMainAddress())
                .build();
            DeliveryAddressList.add(addressListDto);
        }
        return new DeliveryAddressListResponseDto(DeliveryAddressList);
    }

}
