package com.example.rest_docker.common.exception;

import org.springframework.http.HttpStatus;

public enum RestDockerExceptionCode {
    COMMON_BAD_REQUEST_ERROR_EXCEPTION(HttpStatus.BAD_REQUEST, "10001", "요청이 잘못되었습니다."),
    COMMON_SERVER_ERROR_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "10002", "서버에서 예기치못한 에러가 발생하였습니다."),
    JWT_EXPIRED_EXCEPTION(HttpStatus.BAD_REQUEST, "11001", "JWT 만료되었습니다."),
    JWT_VALID_EXCEPTION(HttpStatus.BAD_REQUEST, "11002", "JWT 정보의 유효성검사가 실패하였습니다."),
    API_NEED_TOKEN_EXCEPTION(HttpStatus.BAD_REQUEST, "11003", "요청한 API는 유효성을 가진 Access Token이 필요합니다."),
    JWT_NOT_CORRECT_BODY_EXCEPTION(HttpStatus.BAD_REQUEST, "11003", "JWT 의 구성요소가 알맞게 들어있지 않습니다."),


    KAKAO_JSON_PROCESSING_EXCEPTION(HttpStatus.BAD_REQUEST, "20001", "카카오 Json Processing 과정중에 문제가 발생했습니다."),
    KAKAO_JSON_MAPPING_EXCEPTION(HttpStatus.BAD_REQUEST, "20002", "카카오 Json Mapping 과정중에 문제가 발생했습니다."),
    HTTPCLIENT_ERROR_EXCEPTION(HttpStatus.BAD_REQUEST, "20003", "클라이언트의 인증코드가 틀리거나 이미 사용한 인증코드입니다."),
    KAKAO_LOGOUT_EXCEPTION(HttpStatus.BAD_REQUEST, "20004", "카카오 Logout 과정중에 문제가 발생했습니다."),

    ACCOUNT_NOT_EXIST_OAUTH_ID_EXCEPTION(HttpStatus.BAD_REQUEST, "30001", "Account DB에 OAuth ID의 일치하는 정보가 존재하지 않습니다."),
    REFRESH_TOKEN_NOT_MATCH_INFO_EXCEPTION(HttpStatus.BAD_REQUEST, "30002", "Account DB에 RefreshToken 이 헤더에 보낸 RefreshToken 과 일치하지 않습니다.");

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
