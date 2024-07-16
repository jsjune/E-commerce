package com.product.productapi.controller.req;

import com.product.productapi.usecase.dto.RegisterProductDto;
import java.util.List;
import java.util.Set;
import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;

@Builder
public record ProductRequestDto(
    String name,
    String description,
    Long price,
    Long stock,
    Set<String>tags,
    List<MultipartFile>productImages
) {

    public RegisterProductDto mapToCommand() {
        return RegisterProductDto.builder()
            .name(name)
            .description(description)
            .price(price)
            .stock(stock)
            .tags(tags)
            .productImages(productImages)
            .build();
    }
}
