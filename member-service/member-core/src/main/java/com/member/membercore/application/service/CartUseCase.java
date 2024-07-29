package com.member.membercore.application.service;


import com.member.membercore.application.service.dto.CartResponseDto;

public interface CartUseCase {

    void addCart(Long memberId, Long productId);

    void deleteCart(Long memberId, Long cartId);

    CartResponseDto getCartList(Long memberId);

    void updateCartQuantity(Long memberId, Long cartId, Long quantity);

}
