package com.ecommerce.product.usecase.impl;

import com.ecommerce.common.error.ErrorCode;
import com.ecommerce.common.error.GlobalException;
import com.ecommerce.product.controller.res.ProductListDto;
import com.ecommerce.product.controller.res.ProductListResponseDto;
import com.ecommerce.product.controller.res.ProductResponseDto;
import com.ecommerce.product.entity.Product;
import com.ecommerce.product.entity.ProductImage;
import com.ecommerce.product.repository.ProductRepository;
import com.ecommerce.product.usecase.ProductReadUseCase;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductReadService implements ProductReadUseCase {
    private final ProductRepository productRepository;

    @Override
    public ProductResponseDto getProduct(Long id) {
        return productRepository.findById(id)
            .map(product -> ProductResponseDto.builder()
                .company(product.getSeller().getCompany())
                .phoneNumber(product.getSeller().getPhoneNumber())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .tags(product.getTags())
                .orgProductImages(product.getProductImages().stream()
                    .map(ProductImage::getOrgImageUrl)
                    .collect(Collectors.toList()))
                .build())
            .orElseThrow(() -> new GlobalException(ErrorCode.PRODUCT_NOT_FOUND));
    }

    @Override
    public ProductListResponseDto getProducts(Pageable pageable) {
        Page<Product> products = productRepository.findAll(pageable);
        List<ProductListDto> response = products.getContent().stream()
            .map(this::mapToProductResponse)
            .collect(Collectors.toList());
        return new ProductListResponseDto(response, products.getNumber(), products.getTotalPages());
    }

    @Override
    public Optional<Product> findById(Long productId) {
        return productRepository.findById(productId);
    }

    private ProductListDto mapToProductResponse(Product product) {
        return ProductListDto.builder()
            .id(product.getId())
            .company(product.getSeller().getCompany())
            .phoneNumber(product.getSeller().getPhoneNumber())
            .name(product.getName())
            .description(product.getDescription())
            .price(product.getPrice())
            .tags(product.getTags())
            .orgProductImages(product.getProductImages().stream()
                .map(ProductImage::getThumbnailUrl)
                .collect(Collectors.toList()))
            .build();
    }


}
