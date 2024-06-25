//package com.orderservice.delivery.entity;
//
//import com.orderservice.utils.BaseTimeEntity;
//import jakarta.persistence.Embedded;
//import jakarta.persistence.Entity;
//import jakarta.persistence.GeneratedValue;
//import jakarta.persistence.GenerationType;
//import jakarta.persistence.Id;
//import lombok.AccessLevel;
//import lombok.Builder;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//
//@Entity
//@Getter
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
//public class DeliveryAddress extends BaseTimeEntity {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//    private Long memberId;
//    @Embedded
//    private Address address;
//    private String receiver;
//    private boolean isMainAddress;
//
//    @Builder
//    public DeliveryAddress(Long id, Long memberId, Address address, String alias, String receiver,
//        boolean isMainAddress) {
//        this.id = id;
//        this.memberId = memberId;
//        this.address = address;
//        this.receiver = receiver;
//        this.isMainAddress = isMainAddress;
//    }
//
//    public void setMainAddress(boolean isMainAddress) {
//        this.isMainAddress = isMainAddress;
//    }
//}
