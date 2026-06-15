package kr.ac.baekseok.java_project.dto.request;

public class TableCreateRequest {
    public final String tableNumber;
    public final int capacity;

    public TableCreateRequest(String tableNumber, int capacity) {
        this.tableNumber = tableNumber;
        this.capacity = capacity;
    }
}
