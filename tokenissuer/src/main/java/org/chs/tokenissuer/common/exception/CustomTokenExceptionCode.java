package org.chs.tokenissuer.common.exception;

import org.springframework.http.HttpStatus;

public enum CustomTokenExceptionCode {
    JWT_EXPIRED_EXCEPTION(HttpStatus.BAD_REQUEST, "11001", "JWT 만료되었습니다."),
    JWT_VALID_EXCEPTION(HttpStatus.BAD_REQUEST, "11002", "JWT 정보의 유효성검사가 실패하였습니다."),
    API_NEED_TOKEN_EXCEPTION(HttpStatus.BAD_REQUEST, "11003", "요청한 API는 유효성을 가진 Access Token이 필요합니다."),
    JWT_NOT_CORRECT_BODY_EXCEPTION(HttpStatus.BAD_REQUEST, "11003", "JWT 의 구성요소가 알맞게 들어있지 않습니다."),
    JWT_ISSUE_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "11004", "JWT 를 발급하는 과정에서 에러가 발생하였습니다."),
    SIGNATURE_RESULT_INVALID_TOKEN(HttpStatus.INTERNAL_SERVER_ERROR, "11005", "토큰의 서명이 유효하지 않습니다."),

    ACCOUNT_NOT_EXIST_OAUTH_ID_EXCEPTION(HttpStatus.BAD_REQUEST, "30001", "Account DB에 OAuth ID의 일치하는 정보가 존재하지 않습니다."),
    REFRESH_TOKEN_NOT_MATCH_INFO_EXCEPTION(HttpStatus.BAD_REQUEST, "30002", "Account DB에 RefreshToken 이 헤더에 보낸 RefreshToken 과 일치하지 않습니다.");

    private HttpStatus httpStatus;
    private final String resultCode;
    private final String description;

    CustomTokenExceptionCode(HttpStatus httpStatus, String resultCode, String description) {
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
