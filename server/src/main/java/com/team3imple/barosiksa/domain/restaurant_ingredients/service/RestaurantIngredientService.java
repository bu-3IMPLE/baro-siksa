package com.team3imple.barosiksa.domain.restaurant_ingredients.service;

import com.team3imple.barosiksa.domain.restaurant_ingredients.dto.request.RestaurantIngredientCreateRequest;
import com.team3imple.barosiksa.domain.restaurant_ingredients.dto.request.RestaurantIngredientUpdateRequest;
import com.team3imple.barosiksa.domain.restaurant_ingredients.dto.response.RestaurantIngredientResponse;
import com.team3imple.barosiksa.domain.ingredients.entity.Ingredient;
import com.team3imple.barosiksa.domain.restaurant_ingredients.entity.RestaurantIngredient;
import com.team3imple.barosiksa.domain.ingredients.repository.IngredientRepository;
import com.team3imple.barosiksa.domain.restaurant_ingredients.repository.RestaurantIngredientRepository;
import com.team3imple.barosiksa.domain.restaurants.entity.Restaurant;
import com.team3imple.barosiksa.domain.restaurants.repository.RestaurantRepository;
import com.team3imple.barosiksa.global.error.CustomException;
import com.team3imple.barosiksa.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RestaurantIngredientService {
    private final RestaurantIngredientRepository restaurantIngredientRepository;
    private final RestaurantRepository restaurantRepository;
    private final IngredientRepository ingredientRepository;

    @Transactional
    public Long createRestaurantIngredient(Long memberId, Long restaurantId, RestaurantIngredientCreateRequest request) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_INPUT_VALUE));

        if (!restaurant.getMember().getId().equals(memberId)) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        Ingredient ingredient = ingredientRepository.findById(request.ingredientId())
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_INPUT_VALUE));

        RestaurantIngredient restaurantIngredient = RestaurantIngredient.builder()
                .restaurant(restaurant)
                .ingredient(ingredient)
                .origin(request.origin())
                .stockQuantity(request.stockQuantity())
                .build();

        return restaurantIngredientRepository.save(restaurantIngredient).getId();
    }

    public List<RestaurantIngredientResponse> getRestaurantIngredients(Long restaurantId) {
        if (!restaurantRepository.existsById(restaurantId)) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        return restaurantIngredientRepository.findByRestaurantId(restaurantId).stream()
                .map(RestaurantIngredientResponse::from)
                .toList();
    }

    @Transactional
    public void updateRestaurantIngredient(Long memberId, Long restaurantId, Long restaurantIngredientId, RestaurantIngredientUpdateRequest request) {
        RestaurantIngredient restaurantIngredient = getValidRestaurantIngredient(memberId, restaurantId, restaurantIngredientId);

        restaurantIngredient.updateRestaurantIngredientInfo(request.origin(), request.stockQuantity());
    }

    @Transactional
    public void deleteRestaurantIngredient(Long memberId, Long restaurantId, Long restaurantIngredientId) {
        RestaurantIngredient restaurantIngredient = getValidRestaurantIngredient(memberId, restaurantId, restaurantIngredientId);

        restaurantIngredientRepository.delete(restaurantIngredient);
    }

    private RestaurantIngredient getValidRestaurantIngredient(Long memberId, Long restaurantId, Long restaurantIngredientId) {
        RestaurantIngredient restaurantIngredient = restaurantIngredientRepository.findById(restaurantIngredientId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_INPUT_VALUE));

        if (!restaurantIngredient.getRestaurant().getId().equals(restaurantId)) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        if (!restaurantIngredient.getRestaurant().getMember().getId().equals(memberId)) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        return restaurantIngredient;
    }
}
