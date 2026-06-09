package kr.ac.baekseok.java_project.dto.request;

import java.util.List;

/** 메뉴 수정 요청 (OWNER 권한) */
public class MenuUpdateRequest {
    public String name;
    public int price;
    public String description;
    public List<Long> restaurantIngredientIds;
}
