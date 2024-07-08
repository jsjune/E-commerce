package com.memberservice.usecase;


import com.memberservice.usecase.dto.CartDto;
import com.memberservice.usecase.dto.CartResponseDto;
import java.util.List;

public interface CartUseCase {

    void addCart(Long memberId, Long productId);

    void deleteCart(Long memberId, Long cartId);

    CartResponseDto getCartList(Long memberId);

    void updateCartQuantity(Long memberId, Long cartId, Long quantity);

    void clearCart(Long memberId, List<Long> cartIds);

    List<CartDto> getCartList(Long memberId, List<Long> cartIds);
}
