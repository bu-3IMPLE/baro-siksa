package kr.ac.baekseok.java_project.dto.response;

public class TableResponse {
    public Long tableId;
    public String tableNumber;
    public Integer capacity;
    public String status; // "AVAILABLE" | "RESERVED" | "OCCUPIED"
}
