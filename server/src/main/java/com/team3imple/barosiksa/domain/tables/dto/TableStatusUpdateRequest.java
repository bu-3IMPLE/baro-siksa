package com.team3imple.barosiksa.domain.tables.dto;

import jakarta.validation.constraints.NotBlank;

public record TableStatusUpdateRequest(
        @NotBlank String status
) {}
