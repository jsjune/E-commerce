package com.ecommerce.member.entity;

import com.ecommerce.common.BaseTimeEntity;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Member extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String phoneNumber;
    private String email;
    private String password;
    @Enumerated(EnumType.STRING)
    private UserRole role;
    private String company;
    @OneToMany(mappedBy = "member")
    private List<Cart> carts;

    @Builder
    public Member(Long id, String username, String phoneNumber, String email, String password,
        UserRole role, String company, List<Cart> carts) {
        this.id = id;
        this.username = username;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.password = password;
        this.role = role;
        this.company = company;
        this.carts = carts;
    }

    public void addCart(Cart cart) {
        cart.setMember(this);
        this.carts.add(cart);
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public void updateInfo(String username, String phoneNumber, String email, String company) {
        this.username = username;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.company = company;
    }
}
