package org.chs.restdockerapis.image.presentation.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RmImageResponseDto {

    private boolean imageDeleteResult;
}
