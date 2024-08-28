package org.chs.restdockerapis.image.presentation.dto;

import lombok.Builder;
import org.chs.domain.image.dto.ImageElements;

import java.util.List;

@Builder
public record LsImageResponseDto (
        List<ImageElements> lsImageList
){}

