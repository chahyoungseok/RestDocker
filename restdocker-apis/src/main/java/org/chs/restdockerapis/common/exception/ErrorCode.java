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
    NOT_EXIST_IMAGE_IN_DOCKERHUB(HttpStatus.BAD_REQUEST, "10106", "이미지 이름과 태그 조합이 DockerHub에 있지 않습니다."),
    ARGS_NEED_SUBNET(HttpStatus.BAD_REQUEST, "10107", "GateWay나 IPRange를 명시적으로 설정하려면 Subnet을 명시적으로 설정해야합니다."),
    NOT_VALID_ADDRESS_FORMAT(HttpStatus.BAD_REQUEST, "10108","유효한 IP 주소형식이 아닙니다."),
    MUST_IPRANGE_INTO_SUBNET(HttpStatus.BAD_REQUEST, "10109", "IPRange 는 Subnet 안에 포함되어야합니다"),
    MUST_GATEWAY_INTO_SUBNET(HttpStatus.BAD_REQUEST, "10110", "Gateway 는 Subnet 안에 포함되어야합니다"),
    DUPLICATE_SUBNET(HttpStatus.BAD_REQUEST, "10111", "Subnet이 존재하는 Network와 겹칩니다."),
    IMPOSSIBLE_RM_BRIDGE_NETWORK(HttpStatus.BAD_REQUEST, "10112", "Bridge 네트워크는 삭제할 수 없습니다."),
    ARGS_NEED_NETWORK(HttpStatus.BAD_REQUEST, "10113", "Container IP를 명시적 선언하려면 Network를 명시적으로 설정해야합니다."),
    REMOVE_IMPOSSIBLE_IMAGE_EXIST_CONTAINER(HttpStatus.BAD_REQUEST, "10114","해당 이미지로 만들어진 컨테이너가 존재하여 이미지를 삭제할 수 없습니다."),
    REMOVE_IMPOSSIBLE_NETWORK_EXIST_CONTAINER(HttpStatus.BAD_REQUEST, "10115","해당 네트워크에 할당된 컨테이너가 존재하여 네트워크를 삭제할 수 없습니다."),
    NO_SPACE_DOCKER_HOST_SUBNET(HttpStatus.BAD_REQUEST, "10116", "Docker Host Network 에 더이상 할당받을 IP 공간이 없습니다"),
    NOT_EXIST_IMAGE_IN_HOST(HttpStatus.BAD_REQUEST, "10117", "이미지 이름과 태그 조합이 당신의 Host에 있지 않습니다."),
    NOT_VALID_PORTFOWARDING(HttpStatus.BAD_REQUEST, "10118", " PortForwarding 이 유효성검증 과정에서 실패하였습니다."),
    NOT_VALID_PRIVATEIP(HttpStatus.BAD_REQUEST, "10119", " PrivateIp 가 유효성검증 과정에서 실패하였습니다."),
    NOT_VALID_NAME(HttpStatus.BAD_REQUEST, "10120", "Container Name 이 이미 당신의 Host에 존재하는 이름입니다."),
    NOT_EXIST_NETWORK_IN_HOST(HttpStatus.BAD_REQUEST, "10117", "요청으로 온 Docker Network가 당신의 Host에 있지 않습니다."),
    ALREADY_CONTAINER_IS_RUNNING(HttpStatus.BAD_REQUEST, "10118", "요청한 컨테이너는 이미 실행중입니다."),
    NOT_EXIST_RUNNING_CONTAINER(HttpStatus.BAD_REQUEST, "10119", "실행중인 컨테이너 중 요청한 컨테이너가 없습니다."),
    NOT_EXIST_CONTAINER(HttpStatus.BAD_REQUEST, "10120", "요청한 컨테이너가 존재하지 않습니다."),

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
