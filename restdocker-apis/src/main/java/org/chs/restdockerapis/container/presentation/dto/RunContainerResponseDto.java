package org.chs.restdockerapis.container.presentation.dto;

import lombok.Builder;

@Builder
public record RunContainerResponseDto(
        boolean startResult
) {}
