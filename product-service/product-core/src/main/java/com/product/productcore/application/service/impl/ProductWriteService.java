package com.product.productcore.application.service.impl;

import com.product.productapi.usecase.ProductWriteUseCase;
import com.product.productcore.openfeign.MemberClient;
import com.product.productcore.application.service.dto.ImageUploadEvent;
import com.product.productcore.application.service.dto.MemberDto;
import com.product.productapi.usecase.dto.RegisterProductDto;
import com.product.productcore.infrastructure.entity.Product;
import com.product.productcore.infrastructure.entity.Seller;
import com.product.productcore.infrastructure.repository.ProductRepository;
import com.product.productcore.application.utils.AesUtil;
import com.product.productcore.application.utils.ImageValidator;
import com.product.productapi.common.error.ErrorCode;
import com.product.productapi.common.error.GlobalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ProductWriteService implements ProductWriteUseCase {

    private final MemberClient memberClient;
    private final ProductRepository productRepository;
    private final AesUtil aesUtil;
    private final ApplicationEventPublisher eventPublisher;
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    @CacheEvict(cacheNames = "products", allEntries = true)
    public void createProduct(Long memberId, RegisterProductDto command) throws Exception {
        MemberDto member = memberClient.getMemberInfo(memberId);
        if (member == null) {
            throw new GlobalException(ErrorCode.MEMBER_NOT_FOUND);
        }
        for (MultipartFile productImage : command.productImages()) {
            ImageValidator.validateImageFile(productImage);
        }

        Seller seller = Seller.builder()
            .sellerId(member.memberId())
            .company(member.company())
            .phoneNumber(aesUtil.aesEncode(member.phoneNumber()))
            .build();
        Product product = Product.builder()
            .name(command.name())
            .description(command.description())
            .price(command.price())
            .totalStock(command.stock())
            .soldQuantity(0L)
            .seller(seller)
            .tags(command.tags())
            .productImages(null)
            .build();

        Product findProduct = productRepository.save(product);
        redisTemplate.opsForValue().set("product.stock="+ findProduct.getId(), String.valueOf(findProduct.getTotalStock()));
        ImageUploadEvent event = new ImageUploadEvent(command.productImages(), member.company(),
            findProduct.getId());
        eventPublisher.publishEvent(event);
    }
}
