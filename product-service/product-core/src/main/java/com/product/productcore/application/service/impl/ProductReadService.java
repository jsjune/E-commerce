package com.product.productcore.application.service.impl;

import com.product.productapi.usecase.ProductReadUseCase;
import com.product.productapi.usecase.dto.ProductListDto;
import com.product.productapi.usecase.dto.ProductListResponseDto;
import com.product.productapi.usecase.dto.ProductResponseDto;
import com.product.productcore.application.utils.AesUtil;
import com.product.productcore.application.utils.ExceptionWrapper;
import com.product.productcore.application.utils.RedisUtils;
import com.product.productcore.infrastructure.entity.Product;
import com.product.productcore.infrastructure.entity.ProductImage;
import com.product.productcore.infrastructure.repository.ProductRepository;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductReadService implements ProductReadUseCase {
    private final ProductRepository productRepository;
    private final AesUtil aesUtil;
    private final RedisUtils redisUtils;

    @Override
    public ProductResponseDto getProduct(Long productId) throws Exception {
        Optional<Product> findProduct = productRepository.findById(productId);
        if (findProduct.isPresent()) {
            Product product = findProduct.get();
            Long totalStock = redisUtils.getStock(String.format("product.stock=%d", productId));
            return ProductResponseDto.builder()
                .sellerId(product.getSeller().getSellerId())
                .company(product.getSeller().getCompany())
                .phoneNumber(aesUtil.aesDecode(product.getSeller().getPhoneNumber()))
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .tags(product.getTags())
                .totalStock(totalStock)
                .orgProductImages(product.getProductImages().stream()
                    .map(ProductImage::getOrgImageUrl)
                    .collect(Collectors.toList()))
                .build();
        }
        return null;
    }

    @Override
    @Cacheable(cacheNames = "products", key = "#type.concat(#keyword).concat(#pageable.pageNumber).concat(#pageable.sort.toString())")
    public ProductListResponseDto getProducts(String type, String keyword, Pageable pageable) {
        List<Product> products = productRepository.searchAll(type, keyword, pageable);
        boolean hasNext = false;
        if (products.size() > pageable.getPageSize()) {
            hasNext = true;
            products = products.subList(0, pageable.getPageSize());
        }
        List<ProductListDto> response = products.stream()
            .map(ExceptionWrapper.wrap(this::mapToProductResponse))
            .toList();
        return ProductListResponseDto.builder()
            .products(response)
            .hasNext(hasNext)
            .build();
    }

    private ProductListDto mapToProductResponse(Product product) throws Exception {
        Set<String> tags = product.getTags() != null ? new HashSet<>(product.getTags()) : new HashSet<>();
        List<String> orgProductImages = product.getProductImages() != null ?
            product.getProductImages().stream()
                .map(ProductImage::getThumbnailUrl)
                .toList()
            : new ArrayList<>();
        return ProductListDto.builder()
            .productId(product.getId())
            .sellerId(product.getSeller().getSellerId())
            .company(product.getSeller().getCompany())
            .phoneNumber(aesUtil.aesDecode(product.getSeller().getPhoneNumber()))
            .name(product.getName())
            .description(product.getDescription())
            .price(product.getPrice())
            .tags(tags)
            .orgProductImages(orgProductImages)
            .build();
    }


}
