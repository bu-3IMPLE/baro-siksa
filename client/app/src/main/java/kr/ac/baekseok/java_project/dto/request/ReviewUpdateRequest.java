package kr.ac.baekseok.java_project.dto.request;

/** 리뷰 수정 요청 (작성자 본인) */
public class ReviewUpdateRequest {
    public int rating;
    public String comment;

    public ReviewUpdateRequest(int rating, String comment) {
        this.rating = rating;
        this.comment = comment;
    }
}
