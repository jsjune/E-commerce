package com.product.productcore.infrastructure.repository;

import com.product.productcore.infrastructure.entity.ProductOutBox;
import java.time.LocalDateTime;
import java.util.List;

public interface CustomProductOutBoxRepository {
    List<ProductOutBox> findAllBySuccessFalseNoOffset(LocalDateTime now, int limit);
}
