package com.example.rest_docker.account.presentation.dto.kakao;

import jakarta.validation.constraints.NotEmpty;

public record KakaoOAuthLoginRequestDto(
        @NotEmpty
        String code
) {}
