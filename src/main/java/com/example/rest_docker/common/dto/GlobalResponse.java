package com.example.rest_docker.common.dto;

import lombok.Builder;

@Builder
public class GlobalResponse {
    private String resultCode;
    private String description;
}
