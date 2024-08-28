package org.chs.restdockerapis.command.presentation.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;

import java.util.List;

@Builder
public record CommandAnalysisResponseDto(
        @NotEmpty
        String url,
        List<String> argCommands
) {}
