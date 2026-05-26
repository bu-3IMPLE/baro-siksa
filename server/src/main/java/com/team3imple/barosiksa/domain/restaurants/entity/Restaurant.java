package com.team3imple.barosiksa.domain.restaurants.entity;

import com.team3imple.barosiksa.domain.member.entity.Member;
import com.team3imple.barosiksa.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalTime;

@Entity
@Table(name = "restaurants")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Restaurant extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "restaurant_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private RestaurantCategory category;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false, precision = 10, scale = 8)
    private BigDecimal latitude;

    @Column(nullable = false, precision = 11, scale = 8)
    private BigDecimal longitude;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "open_time", nullable = false)
    private LocalTime openTime;

    @Column(name = "close_time", nullable = false)
    private LocalTime closeTime;

    @Column(name = "break_start_time")
    private LocalTime breakStartTime;

    @Column(name = "break_end_time")
    private LocalTime breakEndTime;

    @Column(name = "closed_days", length = 100)
    private String closedDays;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    @Builder
    public Restaurant(Member member, String name, RestaurantCategory category,
                      String address, BigDecimal latitude, BigDecimal longitude,
                      String phoneNumber, String description, LocalTime openTime, LocalTime closeTime,
                      LocalTime breakStartTime, LocalTime breakEndTime, String closedDays) {
        this.member = member;
        this.name = name;
        this.category = category;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.phoneNumber = phoneNumber;
        this.description = description;
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.breakStartTime = breakStartTime;
        this.breakEndTime = breakEndTime;
        this.closedDays = closedDays;
        this.isDeleted = false;
    }

    public void updateRestaurantInfo(String name, RestaurantCategory category, String address,
                                     BigDecimal latitude, BigDecimal longitude, String phoneNumber,
                                     String description, LocalTime openTime, LocalTime closeTime,
                                     LocalTime breakStartTime, LocalTime breakEndTime, String closedDays) {
        this.name = name;
        this.category = category;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.phoneNumber = phoneNumber;
        this.description = description;
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.breakStartTime = breakStartTime;
        this.breakEndTime = breakEndTime;
        this.closedDays = closedDays;
    }

    public void deleteRestaurant() {
        this.isDeleted = true;
    }
}