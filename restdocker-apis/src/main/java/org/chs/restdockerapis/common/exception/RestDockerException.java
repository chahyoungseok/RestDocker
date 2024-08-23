package org.chs.restdockerapis.common.exception;

import org.chs.globalutils.dto.GlobalResponse;
import org.springframework.core.NestedRuntimeException;
import org.springframework.http.ResponseEntity;

public abstract class RestDockerException extends NestedRuntimeException {

    private ErrorCode exceptionCode;
    private String message;

    protected RestDockerException(ErrorCode exceptionCode) {
        super(exceptionCode.getDescription());
        this.exceptionCode = exceptionCode;
    }

    protected RestDockerException(ErrorCode exceptionCode, Throwable cause) {
        super(exceptionCode.getResultCode(), cause);
        this.exceptionCode = exceptionCode;
    }

    protected RestDockerException(ErrorCode exceptionCode, String message) {
        super(message);
        this.message = message;
        this.exceptionCode = exceptionCode;
    }

    public ErrorCode getErrorCode() {
        return exceptionCode;
    }

    public ResponseEntity<GlobalResponse> makeResponseEntity() {
        return ResponseEntity.status(exceptionCode.getHttpStatus())
                .body(GlobalResponse.builder()
                        .resultCode(exceptionCode.getResultCode())
                        .description(null == message ? exceptionCode.getDescription() : message)
                        .build()
                );
    }
}
