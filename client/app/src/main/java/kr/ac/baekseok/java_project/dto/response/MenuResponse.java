package kr.ac.baekseok.java_project.dto.response;

import java.io.Serializable;
import java.util.List;

/**
 * 메뉴 응답.
 * inDangerous: 회원의 알레르기/취향 기준으로 위험한 메뉴인지 서버가 판단한 값.
 */
public class MenuResponse implements Serializable {
    public long id;
    public String name;
    public int price;
    public String description;
    public List<MenuIngredientSummary> ingredients;
    public boolean inDangerous;
}
