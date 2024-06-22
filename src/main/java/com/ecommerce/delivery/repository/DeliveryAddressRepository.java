package com.ecommerce.delivery.repository;

import com.ecommerce.delivery.entity.DeliveryAddress;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryAddressRepository extends JpaRepository<DeliveryAddress, Long> {

    List<DeliveryAddress> findAllByMemberId(Long memberId);

    Optional<DeliveryAddress> findByMemberIdAndIsMainAddress(Long memberId, boolean isMainAddress);
}
