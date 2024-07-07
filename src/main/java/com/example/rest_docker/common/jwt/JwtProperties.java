package com.example.rest_docker.common.jwt;

import org.springframework.beans.factory.annotation.Value;

public class JwtProperties {

    @Value("${yml 에서 가져와야하는 경로}")
    public static String SECRET_KEY;

    public static final int ACCESS_TOKEN_EXPIRATION_TIME = 1000 * 60 * 60 * 6; // 6시간 (1/1000초)

    public static final int REFRESH_TOKEN_EXPIRATION_TIME = 1000 * 60 * 60 * 24 * 10; // 10일 (1/1000초)

    public static final String TOKEN_PREFIX = "Bearer ";

    public static final String HEADER_STRING = "Authorization";

    public static final String TYPE = "typ";

    public static final String JWT_TYPE = "JWT";

    public static final String ALGORITHM = "alg";

    public static final String HMAC512 = "HMAC512";

}
