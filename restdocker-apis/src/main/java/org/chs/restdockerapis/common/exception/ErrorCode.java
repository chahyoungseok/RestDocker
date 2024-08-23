package org.chs.restdockerapis.common.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    COMMON_BAD_REQUEST_ERROR_EXCEPTION(HttpStatus.BAD_REQUEST, "10001", "요청이 잘못되었습니다."),
    COMMON_SERVER_ERROR_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "10002", "서버에서 예기치못한 에러가 발생하였습니다."),

    LOGIN_HISTORY_SAVE_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "10101", "로그인 히스토리 저장시 문제가 발생하였습니다."),
    LOGOUT_HISTORY_SAVE_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "10102", "로그아웃 히스토리 저장시 문제가 발생하였습니다."),
    ARGUMENT_NOT_VALID_EXCEPTION(HttpStatus.BAD_REQUEST, "10103", "Request DTO Validation 에서 문제가 발생하였습니다."),
    API_NEED_COMMAND(HttpStatus.BAD_REQUEST, "10104", "Command 가 필요한 API 입니다."),
    ARGUMENT_COMMAND_NOT_VALID_EXCEPTION(HttpStatus.BAD_REQUEST, "10105", "ArgCommand 가 지원하지 않는 형태입니다."),
    DOMAIN_EXCEPTION(HttpStatus.BAD_REQUEST, "10106", "도메인 관련 로직에서 Exception이 발생하였습니다"),

    JWT_EXPIRED_EXCEPTION(HttpStatus.BAD_REQUEST, "11001", "JWT 만료되었습니다."),
    JWT_VALID_EXCEPTION(HttpStatus.BAD_REQUEST, "11002", "JWT 정보의 유효성검사가 실패하였습니다."),
    API_NEED_TOKEN_EXCEPTION(HttpStatus.BAD_REQUEST, "11003", "요청한 API는 유효성을 가진 Access Token이 필요합니다."),
    JWT_NOT_CORRECT_BODY_EXCEPTION(HttpStatus.BAD_REQUEST, "11003", "JWT 의 구성요소가 알맞게 들어있지 않습니다."),
    JWT_ISSUE_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "11004", "JWT 를 발급하는 과정에서 에러가 발생하였습니다."),
    SIGNATURE_RESULT_INVALID_TOKEN(HttpStatus.INTERNAL_SERVER_ERROR, "11005", "토큰의 서명이 유효하지 않습니다."),

    JSON_PROCESSING_EXCEPTION(HttpStatus.BAD_REQUEST, "20001", "Json Processing 과정중에 문제가 발생했습니다."),
    JSON_MAPPING_EXCEPTION(HttpStatus.BAD_REQUEST, "20002", "Json Mapping 과정중에 문제가 발생했습니다."),
    HTTPCLIENT_ERROR_EXCEPTION(HttpStatus.BAD_REQUEST, "20003", "클라이언트의 인증코드가 틀리거나 이미 사용한 인증코드입니다."),
    KAKAO_LOGOUT_EXCEPTION(HttpStatus.BAD_REQUEST, "20004", "카카오 Logout 과정중에 문제가 발생했습니다."),
    NULL_POINT_EXCEPTION(HttpStatus.BAD_REQUEST, "20005", "Third Party 에서 응답받은 값 중 NULL 인값이 존재합니다."),
    NAVER_LOGOUT_EXCEPTION(HttpStatus.BAD_REQUEST, "20104", "네이버 Logout 과정중에 문제가 발생했습니다."),

    ACCOUNT_NOT_EXIST_OAUTH_ID_EXCEPTION(HttpStatus.BAD_REQUEST, "30001", "Account DB에 OAuth ID의 일치하는 정보가 존재하지 않습니다."),
    REFRESH_TOKEN_NOT_MATCH_INFO_EXCEPTION(HttpStatus.BAD_REQUEST, "30002", "Account DB에 RefreshToken 이 헤더에 보낸 RefreshToken 과 일치하지 않습니다."),
    COMMAND_NEED_DOCKER(HttpStatus.BAD_REQUEST, "30003", "명령어의 시작이 docker 가 아닙니다"),
    BLANK_COMMAND(HttpStatus.BAD_REQUEST, "30005", "명령어가 비어있습니다"),
    NOT_CORRECT_MAINCOMMAND(HttpStatus.BAD_REQUEST, "30006", "MainCommand 가 올바르지 않습니다"),
    NOT_CORRECT_SUBCOMMAND(HttpStatus.BAD_REQUEST, "30007", "SubCommand 가 올바르지 않습니다"),

    THIRD_PARTY_CLIENT_EXCEPTION(HttpStatus.BAD_REQUEST, "40001", "Third Party 와의 통신중 Client 의 문제로 에러가 발생하였습니다."),
    THIRD_PARTY_AUTHORIZATION_SERVER_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "40002", "Third Party 와의 통신중 인증 서버의 문제로 에러가 발생하였습니다.");


    private HttpStatus httpStatus;
    private final String resultCode;
    private final String description;

    ErrorCode(HttpStatus httpStatus, String resultCode, String description) {
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
