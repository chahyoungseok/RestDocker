package com.example.rest_docker.common.exception;

import org.springframework.http.HttpStatus;

public enum RestDockerExceptionCode {
    COMMON_BAD_REQUEST_ERROR_EXCEPTION(HttpStatus.BAD_REQUEST, "10001", "요청이 잘못되었습니다."),
    COMMON_SERVER_ERROR_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "10002", "서버에서 예기치못한 에러가 발생하였습니다.");

    private HttpStatus httpStatus;
    private final String resultCode;
    private final String description;

    RestDockerExceptionCode(HttpStatus httpStatus, String resultCode, String description) {
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
