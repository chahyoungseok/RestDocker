package org.chs.restdockerapis.container.presentation.dto;

import lombok.Builder;

@Builder
public record RenameContainerResponseDto(
        boolean renameResult
) {}
