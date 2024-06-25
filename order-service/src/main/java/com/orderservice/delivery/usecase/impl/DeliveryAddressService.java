//package com.orderservice.delivery.usecase.impl;
//
//import com.orderservice.delivery.controller.req.AddressRequestDto;
//import com.orderservice.delivery.controller.res.DeliveryAddressListDto;
//import com.orderservice.delivery.controller.res.DeliveryAddressListResponseDto;
//import com.orderservice.delivery.entity.Address;
//import com.orderservice.delivery.entity.DeliveryAddress;
//import com.orderservice.delivery.repository.DeliveryAddressRepository;
//import com.orderservice.delivery.usecase.DeliveryAddressUseCase;
//import com.orderservice.utils.AesUtil;
//import java.util.ArrayList;
//import java.util.Comparator;
//import java.util.List;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//@Service
//@Transactional
//@RequiredArgsConstructor
//public class DeliveryAddressService implements DeliveryAddressUseCase {
//
//    private final DeliveryAddressRepository deliveryAddressRepository;
//    private final AesUtil aesUtil;
//
//    @Override
//    public void registerAddress(Long memberId, AddressRequestDto request) throws Exception {
//        deliveryAddressRepository.findByMemberIdAndIsMainAddress(memberId, true)
//            .ifPresent(deliveryAddress -> deliveryAddress.setMainAddress(false));
//        Address address = Address.builder()
//            .street(aesUtil.aesEncode(request.getStreet()))
//            .detailAddress(aesUtil.aesEncode(request.getDetailAddress()))
//            .zipCode(aesUtil.aesEncode(request.getZipCode()))
//            .alias(request.getAlias())
//            .build();
//        DeliveryAddress deliveryAddress = DeliveryAddress.builder()
//            .memberId(memberId)
//            .address(address)
//            .receiver(request.getReceiver())
//            .isMainAddress(request.isMainAddress())
//            .build();
//        deliveryAddressRepository.save(deliveryAddress);
//    }
//
//    @Override
//    public DeliveryAddressListResponseDto getAddresses(Long memberId) throws Exception {
//        List<DeliveryAddress> deliveryAddresses = deliveryAddressRepository.findAllByMemberId(
//                memberId)
//            .stream()
//            .sorted(Comparator.comparing(DeliveryAddress::isMainAddress, Comparator.reverseOrder()))
//            .toList();
//        List<DeliveryAddressListDto> DeliveryAddressList = new ArrayList<>();
//        for (DeliveryAddress deliveryAddress : deliveryAddresses) {
//            DeliveryAddressListDto addressListDto = DeliveryAddressListDto.builder()
//                .deliveryAddressId(deliveryAddress.getId())
//                .street(aesUtil.aesDecode(deliveryAddress.getAddress().getStreet()))
//                .detailAddress(aesUtil.aesDecode(deliveryAddress.getAddress().getDetailAddress()))
//                .zipCode(aesUtil.aesDecode(deliveryAddress.getAddress().getZipCode()))
//                .alias(deliveryAddress.getAddress().getAlias())
//                .receiver(deliveryAddress.getReceiver())
//                .isMainAddress(deliveryAddress.isMainAddress())
//                .build();
//            DeliveryAddressList.add(addressListDto);
//        }
//        return new DeliveryAddressListResponseDto(DeliveryAddressList);
//    }
//
//}
