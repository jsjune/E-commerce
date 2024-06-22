package com.ecommerce.delivery.controller;

import com.ecommerce.common.Response;
import com.ecommerce.delivery.controller.req.AddressRequestDto;
import com.ecommerce.delivery.controller.res.DeliveryAddressListResponseDto;
import com.ecommerce.delivery.usecase.DeliveryAddressUseCase;
import com.ecommerce.member.auth.LoginUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DeliveryController {
    private final DeliveryAddressUseCase deliveryAddressUseCase;

    @PostMapping("/delivery/addresses")
    public Response<Void> registerAddress(@AuthenticationPrincipal LoginUser loginUser, @RequestBody AddressRequestDto request)
        throws Exception {
        deliveryAddressUseCase.registerAddress(loginUser.getMember().getId(), request);
        return Response.success(HttpStatus.OK.value(), null);
    }

    @GetMapping("/delivery/addresses")
    public Response<DeliveryAddressListResponseDto> getAddresses(@AuthenticationPrincipal LoginUser loginUser)
        throws Exception {
        return Response.success(HttpStatus.OK.value(), deliveryAddressUseCase.getAddresses(loginUser.getMember().getId()));
    }


}
