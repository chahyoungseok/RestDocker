package org.chs.restdockerapis.command.presentation.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

@Getter
public class CommandRequestDto {

    @NotEmpty
    private String command;
}
