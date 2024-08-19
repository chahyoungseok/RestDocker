package org.chs.restdockerapis.command.presentation.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;

@Builder
public record CommandAnalysisRequestDto (
        @NotEmpty
        String command
) {}
