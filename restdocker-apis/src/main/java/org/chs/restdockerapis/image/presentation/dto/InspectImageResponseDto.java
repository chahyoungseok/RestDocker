package org.chs.restdockerapis.image.presentation.dto;

import lombok.Builder;
import lombok.Getter;
import org.chs.domain.image.dto.ImageDetailElements;

@Getter
@Builder
public class InspectImageResponseDto {

    private ImageDetailElements inspectImage;
}
