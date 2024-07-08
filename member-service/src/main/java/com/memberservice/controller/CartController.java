package com.memberservice.controller;


import com.memberservice.usecase.dto.CartResponseDto;
import com.memberservice.usecase.CartUseCase;
import com.memberservice.utils.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/carts")
public class CartController {
    private final CartUseCase cartUseCase;

    @PostMapping("/add/{productId}")
    public Response<Void> addCart(@RequestHeader("Member-Id")Long memberId, @PathVariable Long productId){
        cartUseCase.addCart(memberId,productId);
        return Response.success(HttpStatus.OK.value(), null);
    }

    @DeleteMapping("/{cartId}")
    public Response<Void> deleteCart(@RequestHeader("Member-Id")Long memberId, @PathVariable Long cartId){
        cartUseCase.deleteCart(memberId,cartId);
        return Response.success(HttpStatus.OK.value(), null);
    }

    @GetMapping
    public Response<CartResponseDto> getCartList(@RequestHeader("Member-Id")Long memberId){
        return Response.success(HttpStatus.OK.value(), cartUseCase.getCartList(memberId));
    }

    @PostMapping("/{cartId}")
    public Response<Void> updateCartQuantity(@RequestHeader("Member-Id")Long memberId, @PathVariable Long cartId,@RequestParam Long quantity){
        cartUseCase.updateCartQuantity(memberId,cartId,quantity);
        return Response.success(HttpStatus.OK.value(), null);
    }
}
