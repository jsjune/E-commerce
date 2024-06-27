package com.productservice.entity;

import com.productservice.utils.BaseTimeEntity;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import java.util.List;
import java.util.Set;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
public class Product extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private Long price;
    private Long totalStock;
    private Long soldQuantity;
    @Embedded
    private Seller seller;
    @ElementCollection
    @CollectionTable(
        name = "product_tags",
        indexes = @Index(name = "idx_tag", columnList = "tags")
    )
    @BatchSize(size = 100)
    private Set<String> tags;
    @ElementCollection
    @CollectionTable(name = "product_images")
    @BatchSize(size = 100)
    private List<ProductImage> productImages;

    public void decreaseStock(Long quantity) {
        this.totalStock -= quantity;
        this.soldQuantity += quantity;
    }

    public void incrementStock(Long quantity) {
        this.totalStock += quantity;
        this.soldQuantity -= quantity;
    }
}
