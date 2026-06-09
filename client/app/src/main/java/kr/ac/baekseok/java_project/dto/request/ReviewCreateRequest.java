package kr.ac.baekseok.java_project.dto.request;

/**
 * 리뷰 등록 요청 (USER 권한)
 * rating: 1~5, reservationId당 1개만 작성 가능
 */
public class ReviewCreateRequest {
    public long reservationId;
    public int rating;
    public String comment;

    public ReviewCreateRequest(long reservationId, int rating, String comment) {
        this.reservationId = reservationId;
        this.rating = rating;
        this.comment = comment;
    }
}
