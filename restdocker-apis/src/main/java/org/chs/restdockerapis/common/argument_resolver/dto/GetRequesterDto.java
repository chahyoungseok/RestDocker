package org.chs.restdockerapis.common.argument_resolver.dto;

import lombok.Builder;
import org.chs.domain.common.enumerate.ThirdPartyEnum;

@Builder
public record GetRequesterDto(
        String ipAddress,
        String id,
        String oauthAccessToken,
        String oauthRefreshToken,
        ThirdPartyEnum thirdPartyType
){}
