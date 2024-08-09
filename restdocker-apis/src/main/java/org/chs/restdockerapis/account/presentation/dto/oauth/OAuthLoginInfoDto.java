package org.chs.restdockerapis.account.presentation.dto.oauth;

import lombok.Builder;

@Builder
public record OAuthLoginInfoDto (
        String id,
        String nickname,
        String accessToken,
        String refreshToken
) {}