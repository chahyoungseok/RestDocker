package com.example.rest_docker.account.presentation.dto;

import lombok.Builder;

@Builder
public record OAuthLoginResponse(
        String accessToken,
        String refreshToken
) {}



