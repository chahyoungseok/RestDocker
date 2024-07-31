package org.chs.restdockerapis.common.exception;

import lombok.Getter;
import org.chs.globalutils.dto.GlobalResponse;
import org.springframework.http.ResponseEntity;

@Getter
public class RestDockerException extends Exception {

    private RestDockerExceptionCode exceptionCode;

    public RestDockerException(RestDockerExceptionCode exceptionCode) {
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
