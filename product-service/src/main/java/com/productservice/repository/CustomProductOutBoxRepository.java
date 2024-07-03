package com.productservice.repository;

import com.productservice.entity.ProductOutBox;
import java.time.LocalDateTime;
import java.util.List;

public interface CustomProductOutBoxRepository {
    List<ProductOutBox> findAllBySuccessFalseNoOffset(LocalDateTime now, int limit);
}
