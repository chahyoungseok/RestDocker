package org.chs.globalutils.dto;

import lombok.Builder;

@Builder
public record TokenDto (
        String accessToken,
        String refreshToken
){}