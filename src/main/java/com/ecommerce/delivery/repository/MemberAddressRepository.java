package com.ecommerce.delivery.repository;

import com.ecommerce.delivery.entity.MemberAddress;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberAddressRepository extends JpaRepository<MemberAddress, Long> {

    List<MemberAddress> findAllByMemberId(Long memberId);

    Optional<MemberAddress> findByMemberIdAndIsMainAddress(Long memberId, boolean isMainAddress);
}
