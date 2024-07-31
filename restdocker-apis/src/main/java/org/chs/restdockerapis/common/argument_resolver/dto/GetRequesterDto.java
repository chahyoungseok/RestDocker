package org.chs.restdockerapis.common.argument_resolver.dto;

import lombok.Builder;

@Builder
public record GetRequesterDto(
        String ipAddress,
        String id,
        String oauthAccessToken,
        String oauthRefreshToken
){}
