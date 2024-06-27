package com.productservice.controller.res;


import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@Builder
@ToString
public class ProductResponseDto {
    private Long sellerId;
    private String company;
    private String phoneNumber;
    private String name;
    private String description;
    private Long price;
    private Set<String> tags;
    private List<String> orgProductImages;

}
