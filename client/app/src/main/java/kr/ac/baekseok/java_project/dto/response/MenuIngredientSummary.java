package kr.ac.baekseok.java_project.dto.response;

import java.io.Serializable;

/** 메뉴에 포함된 재료 요약 (알레르기 표시용) */
public class MenuIngredientSummary implements Serializable {
    public long restaurantIngredientId;
    public String name;
    public boolean isAllergenic;
}
