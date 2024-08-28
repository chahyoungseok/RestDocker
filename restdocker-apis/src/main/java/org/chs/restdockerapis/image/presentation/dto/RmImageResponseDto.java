package org.chs.restdockerapis.image.presentation.dto;

import lombok.Builder;

@Builder
public record RmImageResponseDto (
        boolean imageDeleteResult
){}
