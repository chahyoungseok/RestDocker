package com.example.rest_docker.account.presentation.dto;

import jakarta.validation.constraints.NotEmpty;

public record OAuthLoginRequestDto(
        @NotEmpty
        String code
) {}
