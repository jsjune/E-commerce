package com.productservice.repository;

import com.productservice.entity.Product;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface CustomProductRepository {

    List<Product> searchAll(String type, String keyword, Pageable pageable);
}
