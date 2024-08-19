package org.chs.restdockerapis.command.presentation.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import org.chs.restdockerapis.command.enumerate.MainCommandEnum;
import org.chs.restdockerapis.command.enumerate.SubCommandEnum;

import java.util.List;

@Builder
public record SeparateRequestDto(
        @NotEmpty
        MainCommandEnum mainCommand,

        SubCommandEnum subCommand,

        List<String> argCommand
) {}
