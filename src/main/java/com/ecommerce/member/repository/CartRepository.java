package com.ecommerce.member.repository;

import com.ecommerce.member.entity.Cart;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {

    void deleteByIdAndMemberId(Long cartId, Long memberId);

    void deleteAllByMemberIdAndProductIdIn(Long memberId, List<Long> productId);
}
