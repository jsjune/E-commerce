//package com.orderservice.delivery.controller;
//
//import com.orderservice.delivery.controller.req.AddressRequestDto;
//import com.orderservice.delivery.controller.res.DeliveryAddressListResponseDto;
//import com.orderservice.delivery.usecase.DeliveryAddressUseCase;
//import com.orderservice.utils.Response;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestHeader;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/delivery")
//public class DeliveryController {
//    private final DeliveryAddressUseCase deliveryAddressUseCase;
//
//    @PostMapping("/addresses")
//    public Response<Void> registerAddress(@RequestHeader("Member-Id")Long memberId, @RequestBody AddressRequestDto request)
//        throws Exception {
//        deliveryAddressUseCase.registerAddress(memberId, request);
//        return Response.success(HttpStatus.OK.value(), null);
//    }
//
//    @GetMapping("/addresses")
//    public Response<DeliveryAddressListResponseDto> getAddresses(@RequestHeader("Member-Id")Long memberId)
//        throws Exception {
//        return Response.success(HttpStatus.OK.value(), deliveryAddressUseCase.getAddresses(memberId));
//    }
//
//
//}
