package com.ecommerce.member.controller;

import com.ecommerce.common.Response;
import com.ecommerce.member.auth.LoginUser;
import com.ecommerce.member.controller.res.CartResponseDto;
import com.ecommerce.member.usecase.CartUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CartController {
    private final CartUseCase cartUseCase;

    @PostMapping("/carts/add/{productId}")
    public Response<Void> addCart(@AuthenticationPrincipal LoginUser loginUser, @PathVariable Long productId){
        cartUseCase.addCart(loginUser.getMember().getId(),productId);
        return Response.success(HttpStatus.OK.value(), null);
    }

    @DeleteMapping("/carts/{cartId}")
    public Response<Void> deleteCart(@AuthenticationPrincipal LoginUser loginUser, @PathVariable Long cartId){
        cartUseCase.deleteCart(loginUser.getMember().getId(),cartId);
        return Response.success(HttpStatus.OK.value(), null);
    }

    @GetMapping("/carts")
    public Response<CartResponseDto> getCartList(@AuthenticationPrincipal LoginUser loginUser){
        return Response.success(HttpStatus.OK.value(), cartUseCase.getCartList(loginUser.getMember().getId()));
    }

    @PostMapping("/carts/{cartId}")
    public Response<Void> updateCartQuantity(@AuthenticationPrincipal LoginUser loginUser, @PathVariable Long cartId,@RequestParam int quantity){
        cartUseCase.updateCartQuantity(loginUser.getMember().getId(),cartId,quantity);
        return Response.success(HttpStatus.OK.value(), null);
    }
}
