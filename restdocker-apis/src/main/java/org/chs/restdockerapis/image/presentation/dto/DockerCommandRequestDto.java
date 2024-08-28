package org.chs.restdockerapis.image.presentation.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record DockerCommandRequestDto(
        List<String> argCommands
) {}
