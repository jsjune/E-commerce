package com.productservice.repository;

import com.productservice.entity.ProductOutBox;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductOutBoxRepository extends JpaRepository<ProductOutBox, Long>,CustomProductOutBoxRepository {

}
