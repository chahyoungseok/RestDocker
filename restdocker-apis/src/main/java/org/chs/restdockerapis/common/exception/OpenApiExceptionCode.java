package org.chs.restdockerapis.common.exception;

import org.springframework.http.HttpStatus;

public enum OpenApiExceptionCode {
    JSON_PROCESSING_EXCEPTION(HttpStatus.BAD_REQUEST, "20001", "Json Processing 과정중에 문제가 발생했습니다."),
    JSON_MAPPING_EXCEPTION(HttpStatus.BAD_REQUEST, "20002", "Json Mapping 과정중에 문제가 발생했습니다."),
    HTTPCLIENT_ERROR_EXCEPTION(HttpStatus.BAD_REQUEST, "20003", "클라이언트의 인증코드가 틀리거나 이미 사용한 인증코드입니다."),
    KAKAO_LOGOUT_EXCEPTION(HttpStatus.BAD_REQUEST, "20004", "카카오 Logout 과정중에 문제가 발생했습니다."),
    NULL_POINT_EXCEPTION(HttpStatus.BAD_REQUEST, "20005", "Third Party 에서 응답받은 값 중 NULL 인값이 존재합니다."),
    NAVER_LOGOUT_EXCEPTION(HttpStatus.BAD_REQUEST, "20104", "네이버 Logout 과정중에 문제가 발생했습니다.");

    private HttpStatus httpStatus;
    private final String resultCode;
    private final String description;

    OpenApiExceptionCode(HttpStatus httpStatus, String resultCode, String description) {
        this.httpStatus = httpStatus;
        this.resultCode = resultCode;
        this.description = description;
    }

    public HttpStatus getHttpStatus() {
        return this.httpStatus;
    }

    public String getResultCode() {
        return this.resultCode;
    }

    public String getDescription() {
        return this.description;
    }
}
