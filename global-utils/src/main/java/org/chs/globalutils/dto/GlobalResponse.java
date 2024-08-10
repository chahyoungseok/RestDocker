package org.chs.globalutils.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GlobalResponse {
    private String resultCode;
    private String description;
}
