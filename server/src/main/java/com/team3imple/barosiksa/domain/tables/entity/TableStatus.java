package com.team3imple.barosiksa.domain.tables.entity;

public enum TableStatus {
    AVAILABLE,  // 빈 테이블
    RESERVED,   // 예약됨 (PENDING 상태의 예약 존재)
    OCCUPIED    // 착석 중 (CONFIRMED)
}
