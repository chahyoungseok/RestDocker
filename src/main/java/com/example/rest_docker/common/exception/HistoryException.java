package com.example.rest_docker.common.exception;

import com.example.rest_docker.common.dto.GlobalResponse;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

@Getter
public class HistoryException extends Exception {

    private HistoryExceptionCode exceptionCode;

    public HistoryException(HistoryExceptionCode exceptionCode) {
        super(exceptionCode.getDescription());
        this.exceptionCode = exceptionCode;
    }

    public ResponseEntity<GlobalResponse> makeResponseEntity() {
        return ResponseEntity.status(exceptionCode.getHttpStatus())
                .body(GlobalResponse.builder()
                        .resultCode(exceptionCode.getResultCode())
                        .description(exceptionCode.getDescription())
                        .build()
                );
    }
}
