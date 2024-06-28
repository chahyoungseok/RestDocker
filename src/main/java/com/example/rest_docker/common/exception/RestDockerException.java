package com.example.rest_docker.common.exception;

import com.example.rest_docker.common.dto.GlobalResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;

@AllArgsConstructor
public class RestDockerException extends Exception {

    private RestDockerExceptionCode exceptionCode;

    public ResponseEntity<GlobalResponse> makeResponseEntity() {

        return ResponseEntity.status(exceptionCode.getHttpStatus())
                .body(GlobalResponse.builder()
                        .resultCode(exceptionCode.getResultCode())
                        .description(exceptionCode.getDescription())
                        .build()
                );
    }
}
