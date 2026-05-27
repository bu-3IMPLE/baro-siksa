package kr.ac.baekseok.java_project.model;

/**
 * 게시물 댓글/답글 데이터 모델
 */
public class Comment {
    private int id;
    private String writer;
    private String content;
    private String createdAt;

    public Comment(int id, String writer, String content, String createdAt) {
        this.id = id;
        this.writer = writer;
        this.content = content;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public String getWriter() { return writer; }
    public String getContent() { return content; }
    public String getCreatedAt() { return createdAt; }
}
