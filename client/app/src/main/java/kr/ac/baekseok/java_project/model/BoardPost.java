package kr.ac.baekseok.java_project.model;

/**
 * 게시판 게시물 데이터 모델
 */
public class BoardPost {
    private int no;
    private String subject;
    private String writer;
    private int replyCount;

    public BoardPost(int no, String subject, String writer, int replyCount) {
        this.no = no;
        this.subject = subject;
        this.writer = writer;
        this.replyCount = replyCount;
    }

    public int getNo() { return no; }
    public String getSubject() { return subject; }
    public String getWriter() { return writer; }
    public int getReplyCount() { return replyCount; }
}
