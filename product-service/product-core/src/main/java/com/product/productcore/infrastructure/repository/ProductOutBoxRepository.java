package com.product.productcore.infrastructure.repository;

import com.product.productcore.infrastructure.entity.ProductOutBox;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductOutBoxRepository extends JpaRepository<ProductOutBox, Long>,
    CustomProductOutBoxRepository {

}
