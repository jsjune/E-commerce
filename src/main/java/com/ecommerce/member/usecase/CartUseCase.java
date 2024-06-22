package com.ecommerce.member.usecase;

import com.ecommerce.member.controller.res.CartResponseDto;
import java.util.List;

public interface CartUseCase {

    void addCart(Long memberId, Long productId);

    void deleteCart(Long memberId, Long cartId);

    CartResponseDto getCartList(Long memberId);

    void updateCartQuantity(Long memberId, Long cartId, int quantity);

    void clearCart(Long memberId, List<Long> productIds);
}
