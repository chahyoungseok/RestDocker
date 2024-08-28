package org.chs.restdockerapis.image.presentation.dto;

import lombok.Builder;

@Builder
public record PullImageResponseDto(
        String pullImageFullName
) {}
