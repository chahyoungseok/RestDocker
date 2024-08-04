package org.chs.restdockerapis.account.presentation.dto.common;

import lombok.Builder;

@Builder
public record OAuthTokenDto(
        String accessToken,
        String refreshToken
) {}
