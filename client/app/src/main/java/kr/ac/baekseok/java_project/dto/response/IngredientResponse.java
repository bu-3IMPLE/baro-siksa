package kr.ac.baekseok.java_project.dto.response;

import java.io.Serializable;

/** 마스터 재료 응답 */
public class IngredientResponse implements Serializable {
    public long id;
    public String name;
    public boolean isAllergenic;
}
