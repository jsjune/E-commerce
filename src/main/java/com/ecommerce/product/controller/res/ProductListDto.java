package com.ecommerce.product.controller.res;

import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class ProductListDto {
    private Long id;
    private String company;
    private String phoneNumber;
    private String name;
    private String description;
    private int price;
    private Set<String> tags;
    private List<String> orgProductImages;
}
