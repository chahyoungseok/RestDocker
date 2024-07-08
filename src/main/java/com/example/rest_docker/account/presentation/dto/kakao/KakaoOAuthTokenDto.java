package com.example.rest_docker.account.presentation.dto.kakao;

import lombok.Builder;

@Builder
public record KakaoOAuthTokenDto(
        String accessToken,
        String refreshToken
) {}
