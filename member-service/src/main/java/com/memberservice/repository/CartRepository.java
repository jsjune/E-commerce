package com.memberservice.repository;

import com.memberservice.entity.Cart;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {

    void deleteByIdAndMemberId(Long cartId, Long memberId);

    void deleteAllByMemberIdAndIdIn(Long memberId, List<Long> cartIds);
}
