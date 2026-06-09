package kr.ac.baekseok.java_project.dto.response;

import java.io.Serializable;

/** 리뷰 응답 */
public class ReviewResponse implements Serializable {
    public long reviewId;
    public long memberId;
    public int rating;
    public String comment;
    public String createdAt;   // ISO-8601 문자열
}
