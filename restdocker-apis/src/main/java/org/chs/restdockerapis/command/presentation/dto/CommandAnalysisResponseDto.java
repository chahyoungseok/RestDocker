package org.chs.restdockerapis.command.presentation.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class CommandAnalysisResponseDto {

    @NotEmpty
    private String url;

    private List<String> argCommands;
}
