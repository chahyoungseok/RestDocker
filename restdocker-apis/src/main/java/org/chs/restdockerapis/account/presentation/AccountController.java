package org.chs.restdockerapis.account.presentation;

import org.chs.globalutils.dto.TokenDto;
import org.chs.restdockerapis.account.application.AccountService;
import org.chs.restdockerapis.account.presentation.dto.common.GenericSingleResponse;
import org.chs.restdockerapis.account.presentation.dto.common.OAuthLoginRequestDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.chs.restdockerapis.account.presentation.dto.ReIssueTokenRequest;
import org.chs.restdockerapis.account.presentation.dto.ReIssueTokenResponse;
import org.chs.restdockerapis.common.aop.annotation.Auth;
import org.chs.restdockerapis.common.argument_resolver.annotation.GetRequester;
import org.chs.restdockerapis.common.argument_resolver.dto.GetRequesterDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1/account")
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/kakao/login")
    public ResponseEntity<TokenDto> kakaoOAuthLogin(@GetRequester GetRequesterDto requesterInfo, @Valid @RequestBody OAuthLoginRequestDto request) {
        return ResponseEntity.ok(accountService.kakaoOAuthLogin(requesterInfo.ipAddress(), request));
    }

    @Auth
    @PostMapping("/kakao/logout")
    public ResponseEntity<GenericSingleResponse<Boolean>> kakaoOAuthLogout(@GetRequester GetRequesterDto requesterInfo) {
        return ResponseEntity.ok(accountService.kakaoOAuthLogout(requesterInfo));
    }

    @PostMapping("/naver/login")
    public ResponseEntity<TokenDto> naverOAuthLogin(@GetRequester GetRequesterDto requesterInfo, @Valid @RequestBody OAuthLoginRequestDto request) {
        return ResponseEntity.ok(accountService.naverOAuthLogin(requesterInfo.ipAddress(), request));
    }

    @Auth
    @PostMapping("/naver/logout")
    public ResponseEntity<GenericSingleResponse<Boolean>> naverOAuthLogout(@GetRequester GetRequesterDto requesterInfo) {
        return ResponseEntity.ok(accountService.naverOAuthLogout(requesterInfo));
    }

    @GetMapping("/naver/state-value")
    public ResponseEntity<GenericSingleResponse<String>> naverStateValue() {
        return ResponseEntity.ok(accountService.naverStateValue());
    }

    @PostMapping("/reissue")
    public ResponseEntity<ReIssueTokenResponse> reIssueToken(@RequestBody ReIssueTokenRequest request) {
        return ResponseEntity.ok(accountService.reIssueToken(request));
    }
}
