package com.product.productcore.infrastructure.repository;

import com.product.productcore.infrastructure.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long>, CustomProductRepository {

}
