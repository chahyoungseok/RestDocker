package com.example.rest_docker.tokenissuer.application;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.rest_docker.account.presentation.dto.OAuthLoginResponse;
import com.example.rest_docker.common.jwt.JwtProperties;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class TokenIssuerService {

    private Map<String, Object> header;

    public TokenIssuerService() {
        header = new HashMap<>();
        header.put(JwtProperties.TYPE, JwtProperties.JWT_TYPE);
        header.put(JwtProperties.ALGORITHM , JwtProperties.HMAC512);
    }

    public OAuthLoginResponse issueToken(String oauthServiceId, String nickname) {

        String accessToken = this.issueToken(oauthServiceId, nickname, JwtProperties.ACCESS_TOKEN_EXPIRATION_TIME);
        String refreshToken = this.issueToken(oauthServiceId, nickname, JwtProperties.REFRESH_TOKEN_EXPIRATION_TIME);

        return OAuthLoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    private String issueToken(String oauthServiceId, String nickname, int expiredDate) {
        return JWT.create()
                .withHeader(header)
                .withSubject("Rest Docker - JWT Token")
                .withClaim("oauthServiceId", oauthServiceId)
                .withClaim("nickname", nickname)
                .withExpiresAt(new Date(System.currentTimeMillis() + expiredDate))
                .sign(Algorithm.HMAC512(JwtProperties.SECRET_KEY));
    }

}
