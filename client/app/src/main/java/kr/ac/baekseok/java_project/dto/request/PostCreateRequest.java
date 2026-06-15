package kr.ac.baekseok.java_project.dto.request;

public class PostCreateRequest {
    public String title;
    public String content;

    public PostCreateRequest(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
