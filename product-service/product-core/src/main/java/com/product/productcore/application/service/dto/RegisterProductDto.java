package com.product.productcore.application.service.dto;

import java.util.List;
import java.util.Set;
import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;

@Builder
public record RegisterProductDto(
    String name,
    String description,
    Long price,
    Long stock,
    Set<String> tags,
    List<MultipartFile> productImages
) {

}
