package com.example.rest_docker.common.argument_resolver.dto;

import lombok.Builder;

@Builder
public record GetRequesterDto(
        String ipAddress,
        String id,
        String accessToken
){}
