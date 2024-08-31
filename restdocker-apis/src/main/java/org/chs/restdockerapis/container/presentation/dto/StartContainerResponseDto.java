package org.chs.restdockerapis.container.presentation.dto;

import lombok.Builder;

@Builder
public record StartContainerResponseDto(
        boolean startResult
) {}
