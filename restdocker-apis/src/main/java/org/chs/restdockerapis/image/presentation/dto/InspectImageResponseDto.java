package org.chs.restdockerapis.image.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.chs.domain.image.dto.ImageDetailElements;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InspectImageResponseDto {

    private ImageDetailElements inspectImage;
}
