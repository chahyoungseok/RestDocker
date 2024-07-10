package com.example.rest_docker.tokenissuer.presentation.dto;

import lombok.Getter;

@Getter
public class ReIssueTokenRequest {
    private String refreshToken;
}
