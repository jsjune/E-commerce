package com.ecommerce.delivery.entity;

import com.ecommerce.member.entity.Member;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DeliveryAddress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private Member member;
    @Embedded
    private Address address;
    private String receiver;
    private boolean isMainAddress;

    @Builder
    public DeliveryAddress(Long id, Member member, Address address, String alias, String receiver,
        boolean isMainAddress) {
        this.id = id;
        this.member = member;
        this.address = address;
        this.receiver = receiver;
        this.isMainAddress = isMainAddress;
    }

    public void setMainAddress(boolean isMainAddress) {
        this.isMainAddress = isMainAddress;
    }
}
