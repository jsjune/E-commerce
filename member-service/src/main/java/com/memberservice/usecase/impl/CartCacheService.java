package com.memberservice.usecase.impl;

import com.ecommerce.common.cache.CartListDto;
import com.memberservice.entity.Cart;
import com.memberservice.entity.Member;
import com.memberservice.repository.MemberRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartCacheService {

    private final MemberRepository memberRepository;

    @Cacheable(value = "cartList", key = "#memberId")
    public List<CartListDto> getCartList(Long memberId) {
        Optional<Member> findMember = memberRepository.findById(memberId);
        if (findMember.isPresent()) {
            Member member = findMember.get();
            List<CartListDto> cartList = new ArrayList<>();
            for (Cart cart : member.getCarts()) {
                CartListDto cartListDto = CartListDto.builder()
                    .cartId(cart.getId())
                    .productId(cart.getProductId())
                    .productName(cart.getProductName())
                    .price(cart.getPrice())
                    .quantity(cart.getQuantity())
                    .thumbnailImageUrl(cart.getThumbnailUrl())
                    .build();
                cartList.add(cartListDto);
            }
            return cartList;
        }
        return null;
    }

}
