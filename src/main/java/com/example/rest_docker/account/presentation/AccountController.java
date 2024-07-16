package com.example.rest_docker.account.presentation;

import com.example.rest_docker.account.application.AccountService;
import com.example.rest_docker.account.presentation.dto.OAuthLoginRequestDto;
import com.example.rest_docker.account.presentation.dto.OAuthLoginResponse;
import com.example.rest_docker.common.aop.annotation.Auth;
import com.example.rest_docker.common.argument_resolver.annotation.GetRequester;
import com.example.rest_docker.common.argument_resolver.dto.GetRequesterDto;
import com.example.rest_docker.common.exception.RestDockerException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1/account")
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/kakao/login")
    public ResponseEntity<OAuthLoginResponse> kakaoOAuthLogin(@Valid @RequestBody OAuthLoginRequestDto request) throws RestDockerException {
        return ResponseEntity.ok(accountService.kakaoOAuthLogin(request));
    }

    @Auth
    @PostMapping("/kakao/logout")
    public ResponseEntity<?> kakaoOAuthLogout(@GetRequester GetRequesterDto requesterInfo) throws RestDockerException {
        return ResponseEntity.ok(accountService.kakaoOAuthLogout(requesterInfo));
    }

    @PostMapping("/naver/login")
    public ResponseEntity<OAuthLoginResponse> naverOAuthLogin(@Valid @RequestBody OAuthLoginRequestDto request) throws RestDockerException {
        return ResponseEntity.ok(accountService.naverOAuthLogin(request));
    }

    @Auth
    @PostMapping("/naver/logout")
    public ResponseEntity<?> naverOAuthLogout(@GetRequester GetRequesterDto requesterInfo) throws RestDockerException {
        return ResponseEntity.ok(accountService.naverOAuthLogout(requesterInfo));
    }

    @GetMapping("/naver/state-value")
    public ResponseEntity<String> naverStateValue() {
        return ResponseEntity.ok(accountService.naverStateValue());
    }
}
