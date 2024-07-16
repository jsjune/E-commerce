package com.product.productcore.infrastructure.repository;

import com.product.productcore.infrastructure.entity.Product;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface CustomProductRepository {

    List<Product> searchAll(String type, String keyword, Pageable pageable);
}
