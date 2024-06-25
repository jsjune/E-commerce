package com.deliveryservice.controller;


import com.deliveryservice.controller.req.AddressRequestDto;
import com.deliveryservice.controller.res.DeliveryAddressListResponseDto;
import com.deliveryservice.usecase.DeliveryAddressUseCase;
import com.deliveryservice.utils.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/delivery")
public class DeliveryController {
    private final DeliveryAddressUseCase deliveryAddressUseCase;

    @PostMapping("/addresses")
    public Response<Void> registerAddress(@RequestHeader("Member-Id")Long memberId, @RequestBody AddressRequestDto request)
        throws Exception {
        deliveryAddressUseCase.registerAddress(memberId, request.mapToCommand());
        return Response.success(HttpStatus.OK.value(), null);
    }

    @GetMapping("/addresses")
    public Response<DeliveryAddressListResponseDto> getAddresses(@RequestHeader("Member-Id")Long memberId)
        throws Exception {
        return Response.success(HttpStatus.OK.value(), deliveryAddressUseCase.getAddresses(memberId));
    }


}
