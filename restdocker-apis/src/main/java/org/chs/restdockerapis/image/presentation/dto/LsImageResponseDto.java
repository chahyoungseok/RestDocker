package org.chs.restdockerapis.image.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.chs.domain.image.dto.ImageElements;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LsImageResponseDto {

    List<ImageElements> lsImageList;
}

