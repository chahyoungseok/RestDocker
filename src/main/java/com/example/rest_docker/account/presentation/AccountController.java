package com.example.rest_docker.account.presentation;

import com.example.rest_docker.account.application.AccountService;
import com.example.rest_docker.account.presentation.dto.kakao.KakaoOAuthLoginRequestDto;
import com.example.rest_docker.account.presentation.dto.OAuthLoginResponse;
import com.example.rest_docker.common.aop.annotation.Auth;
import com.example.rest_docker.common.argument_resolver.annotation.GetRequester;
import com.example.rest_docker.common.argument_resolver.dto.GetRequesterDto;
import com.example.rest_docker.common.exception.RestDockerException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1/account")
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/kakao/login")
    public ResponseEntity<OAuthLoginResponse> kakaoOAuthLogin(@Valid @RequestBody KakaoOAuthLoginRequestDto request) throws RestDockerException {
        return ResponseEntity.ok(accountService.kakaoOAuthLogin(request));
    }

    @Auth
    @GetMapping("/kakao/logout")
    public ResponseEntity<?> kakaoOAuthLogout(@GetRequester GetRequesterDto requesterInfo) throws RestDockerException {
        return ResponseEntity.ok(accountService.kakaoOAuthLogout(requesterInfo));
    }
}
