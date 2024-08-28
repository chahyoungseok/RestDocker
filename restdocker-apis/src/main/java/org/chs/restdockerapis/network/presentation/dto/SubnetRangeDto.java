package org.chs.restdockerapis.network.presentation.dto;

import lombok.Builder;

@Builder
public record SubnetRangeDto(
        int startAddress,
        int endAddress
) {}
