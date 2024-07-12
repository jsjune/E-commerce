package com.productservice.usecase.impl;

import com.productservice.adapter.MemberClient;
import com.productservice.entity.Product;
import com.productservice.entity.Seller;
import com.productservice.repository.ProductRepository;
import com.productservice.usecase.ProductWriteUseCase;
import com.productservice.usecase.dto.ImageUploadEvent;
import com.productservice.usecase.dto.MemberDto;
import com.productservice.usecase.dto.RegisterProductDto;
import com.productservice.utils.AesUtil;
import com.productservice.utils.ImageValidator;
import com.productservice.utils.error.ErrorCode;
import com.productservice.utils.error.GlobalException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.ApplicationEventPublisher;
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
        ImageUploadEvent event = new ImageUploadEvent(command.productImages(), member.company(),
            findProduct.getId());
        eventPublisher.publishEvent(event);
    }

    @Override
    public int decreaseStock(Long productId, Long quantity) {
        Optional<Product> findProduct = productRepository.findById(productId);
        if (findProduct.isPresent()) {
            Product product = findProduct.get();
            if (product.getTotalStock() < quantity) {
                return -1;
            }
            product.decreaseStock(quantity);
            return 1;
        }
        return -1;
    }

    @Override
    public Boolean incrementStock(Long productId, Long quantity) {
        Optional<Product> findProduct = productRepository.findById(productId);
        if (findProduct.isPresent()) {
            Product product = findProduct.get();
            product.incrementStock(quantity);
            return true;
        }
        return null;
    }
}
