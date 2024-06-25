package com.productservice.usecase.dto;

import java.util.List;
import java.util.Set;
import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;

@Builder
public record RegisterProductDto(
    String name,
    String description,
    int price,
    int stock,
    Set<String>tags,
    List<MultipartFile>productImages
) {

}
