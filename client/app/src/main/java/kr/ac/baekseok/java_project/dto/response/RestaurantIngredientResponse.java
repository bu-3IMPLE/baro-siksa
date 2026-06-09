package kr.ac.baekseok.java_project.dto.response;

import java.io.Serializable;

/** 식당 식재료 응답 */
public class RestaurantIngredientResponse implements Serializable {
    public long restaurantIngredientId;
    public long ingredientId;
    public String name;
    public boolean isAllergenic;
    public String origin;
    public int stockQuantity;
}
