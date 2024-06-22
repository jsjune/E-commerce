package com.ecommerce.product.entity;

import com.ecommerce.common.BaseTimeEntity;
import com.ecommerce.member.entity.Member;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import java.util.List;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Product extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private int price;
    private int totalStock;
    private int soldQuantity;
    @ManyToOne
    private Member seller;
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

    @Builder
    public Product(Long id, String name, String description, int price, int totalStock,
        int soldQuantity, Member seller, Set<String> tags, List<ProductImage> productImages) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.totalStock = totalStock;
        this.soldQuantity = soldQuantity;
        this.seller = seller;
        this.tags = tags;
        this.productImages = productImages;
    }

    public void decreaseStock(int quantity) {
        this.totalStock -= quantity;
        this.soldQuantity += quantity;
    }
}
