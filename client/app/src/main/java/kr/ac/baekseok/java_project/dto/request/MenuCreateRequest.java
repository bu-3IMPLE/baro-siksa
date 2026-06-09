package kr.ac.baekseok.java_project.dto.request;

import java.util.List;

/** 메뉴 등록 요청 (OWNER 권한) */
public class MenuCreateRequest {
    public String name;
    public int price;
    public String description;
    public List<Long> restaurantIngredientIds;
}
