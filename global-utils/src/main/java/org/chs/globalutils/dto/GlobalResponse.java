package org.chs.globalutils.dto;

import lombok.Builder;

@Builder
public class GlobalResponse {
    private String resultCode;
    private String description;
}
