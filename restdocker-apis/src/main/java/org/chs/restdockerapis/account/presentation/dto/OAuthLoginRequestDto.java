package org.chs.restdockerapis.account.presentation.dto;


import jakarta.validation.constraints.NotEmpty;

public record OAuthLoginRequestDto(
        @NotEmpty
        String code
) {}
