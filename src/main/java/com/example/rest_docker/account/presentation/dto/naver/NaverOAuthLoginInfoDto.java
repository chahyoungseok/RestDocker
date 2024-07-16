package com.example.rest_docker.account.presentation.dto.naver;

import lombok.Builder;

@Builder
public record NaverOAuthLoginInfoDto(
        String id,
        String nickname,
        String accessToken,
        String refreshToken
) {}
