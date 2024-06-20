package com.ecommerce.product.controller.res;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProductListResponse {
    private List<ProductList> products;
    private int currentPage;
    private int totalPage;
}
