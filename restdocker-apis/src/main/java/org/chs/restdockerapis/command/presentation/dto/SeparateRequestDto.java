package org.chs.restdockerapis.command.presentation.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import org.chs.restdockerapis.command.enumerate.MainCommandEnum;
import org.chs.restdockerapis.command.enumerate.SubCommandEnum;

import java.util.List;
import java.util.Map;

@Builder
public record SeparateRequestDto(
        @NotEmpty
        MainCommandEnum mainCommand,

        SubCommandEnum subCommand,

        Map<String, List<String>> argCommand
) {}
