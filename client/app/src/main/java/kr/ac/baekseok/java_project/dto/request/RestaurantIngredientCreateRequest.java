package kr.ac.baekseok.java_project.dto.request;

/** 식당 식재료 등록 (OWNER) */
public class RestaurantIngredientCreateRequest {
    public long ingredientId;
    public String origin;
    public int stockQuantity;

    public RestaurantIngredientCreateRequest(long ingredientId, String origin, int stockQuantity) {
        this.ingredientId = ingredientId;
        this.origin = origin;
        this.stockQuantity = stockQuantity;
    }
}
