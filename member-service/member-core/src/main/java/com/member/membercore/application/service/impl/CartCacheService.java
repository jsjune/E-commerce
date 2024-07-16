package com.member.membercore.application.service.impl;

import com.ecommerce.common.cache.CachingCartListDto;
import com.member.membercore.infrastructure.entity.Cart;
import com.member.membercore.infrastructure.entity.Member;
import com.member.membercore.infrastructure.repository.MemberRepository;
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
    public List<CachingCartListDto> getCartList(Long memberId) {
        Optional<Member> findMember = memberRepository.findById(memberId);
        if (findMember.isPresent()) {
            Member member = findMember.get();
            List<CachingCartListDto> cartList = new ArrayList<>();
            for (Cart cart : member.getCarts()) {
                CachingCartListDto cachingCartListDto = CachingCartListDto.builder()
                    .cartId(cart.getId())
                    .productId(cart.getProductId())
                    .productName(cart.getProductName())
                    .price(cart.getPrice())
                    .quantity(cart.getQuantity())
                    .thumbnailImageUrl(cart.getThumbnailUrl())
                    .build();
                cartList.add(cachingCartListDto);
            }
            return cartList;
        }
        return null;
    }

}
