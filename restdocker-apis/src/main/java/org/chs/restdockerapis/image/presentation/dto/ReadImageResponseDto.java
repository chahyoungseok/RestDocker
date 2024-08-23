package org.chs.restdockerapis.image.presentation.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ReadImageResponseDto {

    List<ImageElements> imageList;
}

