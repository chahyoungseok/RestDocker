package com.example.rest_docker.tokenissuer.presentation.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReIssueTokenResponse {
    private String accessToken;
}
