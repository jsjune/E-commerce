package com.memberservice.usecase;


import com.memberservice.adapter.dto.CartDto;
import com.memberservice.controller.res.CartResponseDto;
import java.util.List;

public interface CartUseCase {

    void addCart(Long memberId, Long productId);

    void deleteCart(Long memberId, Long cartId);

    CartResponseDto getCartList(Long memberId);

    void updateCartQuantity(Long memberId, Long cartId, int quantity);

    void clearCart(Long memberId, List<Long> productIds);

    List<CartDto> getCartList(Long memberId, List<Long> cartIds);
}
