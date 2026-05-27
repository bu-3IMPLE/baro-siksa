package com.team3imple.barosiksa.global.error;

import org.springframework.http.ResponseEntity;

public record ErrorResponse(int status, String code, String message) {
    public static ResponseEntity<ErrorResponse> toResponseEntity(ErrorCode errorCode) {
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(new ErrorResponse(
                        errorCode.getHttpStatus().value(),
                        errorCode.name(),
                        errorCode.getMessage()
                ));
    }
}
