package org.chs.restdockerapis.container.presentation.dto;

import lombok.Builder;
import org.chs.domain.container.dto.ContainerElements;

import java.util.List;

@Builder
public record LsContainerResponseDto (
        List<ContainerElements> containerElementsList
){}
