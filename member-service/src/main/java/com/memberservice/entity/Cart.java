package com.memberservice.entity;


import com.memberservice.utils.BaseTimeEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Getter
public class Cart extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long productId;
    private String productName;
    private Long price;
    private String thumbnailUrl;
    private Long quantity;
    @ManyToOne
    private Member member;

    @Builder
    public Cart(Long quantity, Long productId, String productName, Long price, String thumbnailUrl,Member member) {
        this.quantity = quantity;
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.thumbnailUrl = thumbnailUrl;
        this.member = member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public void increaseQuantity(Long quantity) {
        this.quantity += quantity;
    }

    public void updateQuantity(Long quantity) {
        this.quantity = quantity;
    }
}
