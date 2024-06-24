package com.productservice.controller.req;

import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Builder
@AllArgsConstructor
public class ProductRequestDto {
    private String name;
    private String description;
    private int price;
    private int stock;
    private Set<String> tags;
    private List<MultipartFile> productImages;
}
