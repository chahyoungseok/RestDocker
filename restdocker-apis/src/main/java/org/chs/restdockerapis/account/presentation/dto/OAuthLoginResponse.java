package org.chs.restdockerapis.account.presentation.dto;

import lombok.Builder;

@Builder
public record OAuthLoginResponse(
        String accessToken,
        String refreshToken
) {}



