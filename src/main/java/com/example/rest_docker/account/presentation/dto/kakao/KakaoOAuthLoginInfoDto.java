package com.example.rest_docker.account.presentation.dto.kakao;

import lombok.Builder;

@Builder
public record KakaoOAuthLoginInfoDto (
        String id,
        String nickname,
        String accessToken,
        String refreshToken
) {}
