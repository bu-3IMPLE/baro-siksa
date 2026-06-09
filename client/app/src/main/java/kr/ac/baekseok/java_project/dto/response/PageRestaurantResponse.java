package kr.ac.baekseok.java_project.dto.response;

import java.util.List;

/**
 * 식당 검색 페이징 응답.
 * 서버가 Spring Data Page 구조를 그대로 직렬화한 형태.
 */
public class PageRestaurantResponse {
    public long totalElements;
    public int totalPages;
    public int size;
    public List<RestaurantResponse> content;
    public int number;        // 현재 페이지 번호 (0부터)
    public boolean first;
    public boolean last;
    public int numberOfElements;
    public boolean empty;
}
