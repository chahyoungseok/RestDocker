package org.chs.restdockerapis.image.presentation.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class DockerCommandRequestDto {

    private List<String> argCommands;
}
