package kr.ac.baekseok.java_project.dto.request;

/** 식당 식재료 수정 (OWNER) */
public class RestaurantIngredientUpdateRequest {
    public String origin;
    public int stockQuantity;

    public RestaurantIngredientUpdateRequest(String origin, int stockQuantity) {
        this.origin = origin;
        this.stockQuantity = stockQuantity;
    }
}
