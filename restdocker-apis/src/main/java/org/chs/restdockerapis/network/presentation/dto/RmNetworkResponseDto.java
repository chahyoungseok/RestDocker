package org.chs.restdockerapis.network.presentation.dto;

import lombok.Builder;

@Builder
public record RmNetworkResponseDto(
        boolean networkDeleteResult
) {}
