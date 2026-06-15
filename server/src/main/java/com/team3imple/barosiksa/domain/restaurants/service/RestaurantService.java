package com.team3imple.barosiksa.domain.restaurants.service;

import com.team3imple.barosiksa.domain.member.entity.Member;
import com.team3imple.barosiksa.domain.member.repository.MemberRepository;
import com.team3imple.barosiksa.domain.restaurants.dto.request.RestaurantCreateRequest;
import com.team3imple.barosiksa.domain.restaurants.dto.request.RestaurantSearchRequest;
import com.team3imple.barosiksa.domain.restaurants.dto.request.RestaurantUpdateRequest;
import com.team3imple.barosiksa.domain.restaurants.dto.response.RestaurantResponse;
import com.team3imple.barosiksa.domain.restaurants.dto.response.RestaurantSearchCondition;
import com.team3imple.barosiksa.domain.restaurants.entity.Restaurant;
import com.team3imple.barosiksa.domain.restaurants.repository.RestaurantRepository;
import com.team3imple.barosiksa.global.error.CustomException;
import com.team3imple.barosiksa.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RestaurantService {
    private final RestaurantRepository restaurantRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public Long createRestaurant(Long memberId, RestaurantCreateRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Restaurant restaurant = Restaurant.builder()
                .member(member)
                .name(request.name())
                .category(request.category())
                .address(request.address())
                .latitude(request.latitude())
                .longitude(request.longitude())
                .phoneNumber(request.phoneNumber())
                .description(request.description())
                .openTime(request.openTime())
                .closeTime(request.closeTime())
                .breakStartTime(request.breakStartTime())
                .breakEndTime(request.breakEndTime())
                .closedDays(request.closedDays())
                .build();

        return restaurantRepository.save(restaurant).getId();
    }

    public RestaurantResponse getRestaurant(Long restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESTAURANT_NOT_FOUND));

        if (restaurant.isDeleted()) {
            throw new CustomException(ErrorCode.RESTAURANT_NOT_FOUND);
        }

        return RestaurantResponse.from(restaurant);
    }

    @Transactional
    public void updateRestaurant(Long memberId, Long restaurantId, RestaurantUpdateRequest request) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESTAURANT_NOT_FOUND));

        if (restaurant.isDeleted()) {
            throw new CustomException(ErrorCode.RESTAURANT_NOT_FOUND);
        }

        if (!restaurant.getMember().getId().equals(memberId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        restaurant.updateRestaurantInfo(
                request.name(),
                request.category(),
                request.address(),
                request.latitude(),
                request.longitude(),
                request.phoneNumber(),
                request.description(),
                request.openTime(),
                request.closeTime(),
                request.breakStartTime(),
                request.breakEndTime(),
                request.closedDays()
        );
    }

    @Transactional
    public void deleteRestaurant(Long memberId, Long restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESTAURANT_NOT_FOUND));

        if (restaurant.isDeleted()) {
            throw new CustomException(ErrorCode.RESTAURANT_NOT_FOUND);
        }

        if (!restaurant.getMember().getId().equals(memberId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        restaurant.deleteRestaurant();
    }

    public List<RestaurantResponse> searchNearbyRestaurants(RestaurantSearchRequest condition) {
        return restaurantRepository.findByRadius(condition).stream()
                .map(RestaurantResponse::from)
                .toList();
    }

    public Page<RestaurantResponse> searchRestaurants(RestaurantSearchCondition condition, Pageable pageable) {
        return restaurantRepository.searchRestaurants(condition, pageable)
                .map(RestaurantResponse::from);
    }
}
