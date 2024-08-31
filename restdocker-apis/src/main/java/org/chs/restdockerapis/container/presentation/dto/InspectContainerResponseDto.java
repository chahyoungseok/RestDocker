package org.chs.restdockerapis.container.presentation.dto;

import lombok.Builder;
import org.chs.domain.container.dto.ContainerDetailElements;

@Builder
public record InspectContainerResponseDto(
        ContainerDetailElements inspectContainerDetailElements
) {}
