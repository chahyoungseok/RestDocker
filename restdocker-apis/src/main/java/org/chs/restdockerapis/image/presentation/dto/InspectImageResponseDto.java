package org.chs.restdockerapis.image.presentation.dto;

import lombok.Builder;
import org.chs.domain.image.dto.ImageDetailElements;

@Builder
public record InspectImageResponseDto(
        ImageDetailElements inspectImage
){}
