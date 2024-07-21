package com.example.rest_docker.common.exception;

import org.springframework.http.HttpStatus;

public enum HistoryExceptionCode {
    LOGIN_HISTORY_SAVE_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "10001", "로그인 히스토리 저장시 문제가 발생하였습니다."),
    LOGOUT_HISTORY_SAVE_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "10001", "로그아웃 히스토리 저장시 문제가 발생하였습니다.");

    private HttpStatus httpStatus;
    private final String resultCode;
    private final String description;

    HistoryExceptionCode(HttpStatus httpStatus, String resultCode, String description) {
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
