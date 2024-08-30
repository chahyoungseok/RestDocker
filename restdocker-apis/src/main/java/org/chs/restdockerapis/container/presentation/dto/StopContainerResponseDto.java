package org.chs.restdockerapis.container.presentation.dto;

import lombok.Builder;

@Builder
public record StopContainerResponseDto(
        boolean stopResult
) {}
