package org.chs.tokenissuer.common.exception;

import lombok.Getter;
import org.chs.globalutils.dto.GlobalResponse;
import org.springframework.http.ResponseEntity;

@Getter
public class CustomTokenException extends Exception {

    private CustomTokenExceptionCode exceptionCode;

    public CustomTokenException(CustomTokenExceptionCode exceptionCode) {
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
