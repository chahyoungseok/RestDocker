package com.example.rest_docker.tokenissuer.presentation;

import com.example.rest_docker.common.exception.RestDockerException;
import com.example.rest_docker.tokenissuer.application.TokenIssuerService;
import com.example.rest_docker.tokenissuer.presentation.dto.ReIssueTokenRequest;
import com.example.rest_docker.tokenissuer.presentation.dto.ReIssueTokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1/token")
public class TokenIssuerController {

    private final TokenIssuerService tokenIssuerService;

    @PostMapping("/reissue")
    public ResponseEntity<ReIssueTokenResponse> reIssueToken(@RequestBody ReIssueTokenRequest request) throws RestDockerException {
        return ResponseEntity.ok(tokenIssuerService.reIssueToken(request));
    }
}