package com.node5.shopservice.shop.domain;

import com.node5.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "\"shop\"", schema = "public")
public class Shop extends BaseEntity {

    @Id
    private UUID id;

    @Column(name = "member_id", nullable = false)
    private UUID memberId;

    @Column(name = "shop_email", nullable = false, length = 100)
    private String shopEmail;

    @Column(name = "shop_name", nullable = false, length = 50)
    private String shopName;

    @Column(name = "shop_phone_number", nullable = false, length = 20)
    private String shopPhoneNumber;

    @Column(name = "shop_registration_number", nullable = false, length = 100)
    private String shopRegistrationNumber;

    @Column(name = "shop_address", nullable = false, length = 100)
    private String shopAddress;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;


}
