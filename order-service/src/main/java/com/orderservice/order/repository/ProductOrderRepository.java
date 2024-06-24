package com.orderservice.order.repository;


import com.orderservice.order.entity.ProductOrder;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductOrderRepository extends JpaRepository<ProductOrder, Long> {

    Optional<ProductOrder> findByIdAndMemberId(Long orderId, Long memberId);

    List<ProductOrder> findAllByMemberId(Long memberId);
}
