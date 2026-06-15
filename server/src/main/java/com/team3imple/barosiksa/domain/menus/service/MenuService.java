package com.team3imple.barosiksa.domain.menus.service;

import com.team3imple.barosiksa.domain.member.entity.Member;
import com.team3imple.barosiksa.domain.member.repository.MemberRepository;
import com.team3imple.barosiksa.domain.menus.dto.request.MenuCreateRequest;
import com.team3imple.barosiksa.domain.menus.dto.request.MenuUpdateRequest;
import com.team3imple.barosiksa.domain.menus.dto.response.MenuResponse;
import com.team3imple.barosiksa.domain.menus.entity.Menu;
import com.team3imple.barosiksa.domain.menus.entity.MenuIngredient;
import com.team3imple.barosiksa.domain.menus.repository.MenuIngredientRepository;
import com.team3imple.barosiksa.domain.menus.repository.MenuRepository;
import com.team3imple.barosiksa.domain.restaurant_ingredients.entity.RestaurantIngredient;
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
public class MenuService {
    private final MenuRepository menuRepository;
    private final MenuIngredientRepository menuIngredientRepository;
    private final RestaurantRepository restaurantRepository;
    private final RestaurantIngredientRepository restaurantIngredientRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public Long createMenu(Long memberId, Long restaurantId, MenuCreateRequest request) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESTAURANT_NOT_FOUND));

        if (!restaurant.getMember().getId().equals(memberId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        Menu menu = Menu.builder()
                .restaurant(restaurant)
                .name(request.name())
                .price(request.price())
                .description(request.description())
                .build();

        Menu savedMenu = menuRepository.save(menu);

        saveMenuIngredients(savedMenu, restaurantId, request.restaurantIngredientIds());

        return savedMenu.getId();
    }

    public List<MenuResponse> getMenusByRestaurantId(Long restaurantId, Long memberId) {
        if (!restaurantRepository.existsById(restaurantId)) {
            throw new CustomException(ErrorCode.RESTAURANT_NOT_FOUND);
        }

        List<MenuResponse> menus = menuRepository.findMenusWithIngredientsByRestaurantId(restaurantId);

        if (memberId == null) {
            return menus;
        }

        Member member = memberRepository.findById(memberId).orElse(null);
        if (member == null || member.getFoodPreference() == null || member.getFoodPreference().isBlank()) {
            return menus;
        }

        String preferences = member.getFoodPreference();

        return menus.stream()
                .map(menu -> {
                    boolean isDangerous = menu.ingredients().stream()
                            .anyMatch(ingredient -> ingredient.isAllergenic() && preferences.contains(ingredient.name()));

                    return menu.markAsDangerous(isDangerous);
                })
                .toList();
    }

    @Transactional
    public void updateMenu(Long memberId, Long restaurantId, Long menuId, MenuUpdateRequest request) {
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new CustomException(ErrorCode.MENU_NOT_FOUND));

        if (!menu.getRestaurant().getId().equals(restaurantId) ||
                !menu.getRestaurant().getMember().getId().equals(memberId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        menu.updateMenuInfo(request.name(), request.price(), request.description());

        menuIngredientRepository.deleteByMenuId(menuId);

        saveMenuIngredients(menu, restaurantId, request.restaurantIngredientIds());
    }

    @Transactional
    public void deleteMenu(Long memberId, Long restaurantId, Long menuId) {
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new CustomException(ErrorCode.MENU_NOT_FOUND));

        if (!menu.getRestaurant().getId().equals(restaurantId) ||
                !menu.getRestaurant().getMember().getId().equals(memberId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        menuIngredientRepository.deleteByMenuId(menuId);

        menuRepository.delete(menu);
    }

    private void saveMenuIngredients(Menu menu, Long restaurantId, List<Long> ingredientIds) {
        if (ingredientIds != null && !ingredientIds.isEmpty()) {
            List<RestaurantIngredient> ingredients = restaurantIngredientRepository.findAllById(ingredientIds);

            if (ingredients.size() != ingredientIds.size()) {
                throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
            }

            for (RestaurantIngredient ingredient : ingredients) {
                if (!ingredient.getRestaurant().getId().equals(restaurantId)) {
                    throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
                }

                MenuIngredient menuIngredient = MenuIngredient.builder()
                        .menu(menu)
                        .restaurantIngredient(ingredient)
                        .build();

                menuIngredientRepository.save(menuIngredient);
            }
        }
    }
}
