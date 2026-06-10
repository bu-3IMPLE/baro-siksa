package kr.ac.baekseok.java_project.dto.response;

import java.util.List;

public class PagePostResponse {
    public long totalElements;
    public int totalPages;
    public int size;
    public List<PostResponse> content;
    public int number;
    public boolean first;
    public boolean last;
    public int numberOfElements;
    public boolean empty;
}
