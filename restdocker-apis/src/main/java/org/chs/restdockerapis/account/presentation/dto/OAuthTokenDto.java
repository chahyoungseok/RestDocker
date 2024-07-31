package org.chs.restdockerapis.account.presentation.dto;

import lombok.Builder;

@Builder
public record OAuthTokenDto(
        String accessToken,
        String refreshToken
) {}
