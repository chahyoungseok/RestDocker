package org.chs.restdockerapis.image.presentation.dto;

import lombok.Builder;
import lombok.Getter;
import org.chs.domain.image.dto.ImageElements;

import java.util.List;

@Getter
@Builder
public class LsImageResponseDto {

    List<ImageElements> lsImageList;
}

