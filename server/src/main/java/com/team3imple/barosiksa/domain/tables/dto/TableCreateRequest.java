package com.team3imple.barosiksa.domain.tables.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TableCreateRequest(
        @NotBlank(message = "테이블 번호를 입력해주세요.")
        String tableNumber,

        @NotNull(message = "수용 인원을 입력해주세요.")
        @Min(value = 1, message = "수용 인원은 1명 이상이어야 합니다.")
        Integer capacity
) {}
