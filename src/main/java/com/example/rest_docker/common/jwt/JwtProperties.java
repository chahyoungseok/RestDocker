package com.example.rest_docker.common.jwt;

import org.springframework.beans.factory.annotation.Value;

public class JwtProperties {

    @Value("${yml 에서 가져와야하는 경로}")
    public static String SECRET_KEY;

    public static int EXPIRATION_TIME = 864000000; // 10일 (1/1000초)

    public static String TOKEN_PREFIX = "Bearer ";

    public static String HEADER_STRING = "Authorization";

}
