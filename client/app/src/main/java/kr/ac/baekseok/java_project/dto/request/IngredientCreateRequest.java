package kr.ac.baekseok.java_project.dto.request;

/** 마스터 재료 생성 (ADMIN) */
public class IngredientCreateRequest {
    public String name;
    public boolean isAllergenic;

    public IngredientCreateRequest(String name, boolean isAllergenic) {
        this.name = name;
        this.isAllergenic = isAllergenic;
    }
}
