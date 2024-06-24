package com.productservice.controller.res;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProductListResponseDto {
    private List<ProductListDto> products;
    private int currentPage;
    private int totalPage;
}
