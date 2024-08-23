package org.chs.restdockerapis.image.presentation.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public class ImageElements {
    private LocalDateTime createDate;

    private LocalDateTime updateDate;

    private String tag;

    private String os;

    private String size;
}
