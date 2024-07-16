package com.member.membercore.application.service.impl;

import com.member.memberapi.usecase.InternalCartUseCase;
import com.member.memberapi.usecase.dto.CartDto;
import com.member.membercore.infrastructure.repository.CartRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class InternalCartService implements InternalCartUseCase {

    private final CartRepository cartRepository;
    private final CartCacheService cartCacheService;

    @Override
    @CacheEvict(value = "cartList", key = "#memberId")
    public void clearCart(Long memberId, List<Long> cartIds) {
        cartRepository.deleteAllByMemberIdAndIdIn(memberId, cartIds);
    }

    @Override
    public List<CartDto> getCartList(Long memberId, List<Long> cartIds) {
        return cartCacheService.getCartList(memberId).stream()
            .filter(cartListDto -> cartIds.contains(cartListDto.cartId()))
            .map(CartDto::new)
            .toList();
    }
}
