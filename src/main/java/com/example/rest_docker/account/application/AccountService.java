package com.example.rest_docker.account.application;

import com.example.rest_docker.account.domain.AccountRepository;
import com.example.rest_docker.account.domain.entity.AccountEntity;
import com.example.rest_docker.account.presentation.dto.kakao.KakaoOAuthLoginInfoDto;
import com.example.rest_docker.account.presentation.dto.kakao.KakaoOAuthLoginRequestDto;
import com.example.rest_docker.account.presentation.dto.OAuthLoginResponse;
import com.example.rest_docker.account.util.KakaoOAuthUtils;
import com.example.rest_docker.common.exception.RestDockerException;
import com.example.rest_docker.tokenissuer.application.TokenIssuerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccountService {

    private final KakaoOAuthUtils kakaoOAuthUtils;

    private final TokenIssuerService tokenIssuerService;

    private final AccountRepository accountRepository;

    /**
     * @param request
     *    code : 사용자가 카카오 계정로그인을 동의하고 받은 인가코드
     * @return 회원가입 또는 로그인이 완료된 사용자의 정보를 바탕으로 만든 JWT (accessToken, refreshToken)
     * @throws RestDockerException (KAKAO_JSON_PROCESSING_EXCEPTION / KAKAO_JSON_MAPPING_EXCEPTION / HTTPCLIENT_ERROR_EXCEPTION)
     */
    @Transactional(rollbackFor = RestDockerException.class)
    public OAuthLoginResponse kakaoOAuthLogin(KakaoOAuthLoginRequestDto request) throws RestDockerException {
        KakaoOAuthLoginInfoDto kakaoOAuthLoginInfoDto = kakaoOAuthUtils.kakaoOAuthLogin(request.code());
        AccountEntity account = createAccount(kakaoOAuthLoginInfoDto);

        OAuthLoginResponse oAuthLoginResponse = null;
        try {
            // JWT 를 발급하고, RefreshToken을
            oAuthLoginResponse = tokenIssuerService.issueToken(account.getOauthServiceId(), account.getNickname());
            account.setMyServiceToken(oAuthLoginResponse);
        } catch (Exception e) {
            // KaKao OAuth Logout을 한다.
            this.kakaoOAuthUtils.kakaoOAuthLogout(kakaoOAuthLoginInfoDto.accessToken());
        }

        accountRepository.save(account);
        return oAuthLoginResponse;
    }

    private AccountEntity createAccount(KakaoOAuthLoginInfoDto kakaoOAuthLoginInfoDto) {
        Optional<AccountEntity> loginAccount = accountRepository.findByOauthServiceIdEquals(kakaoOAuthLoginInfoDto.id());

        // 1. 로그인이 처음인 경우
        // 2. 회원가입은 되어있지만 활성화되어있지 않은경우
        // 3. 회원가입은 되어있고 활성화도 되어있는 경우

        AccountEntity account = null;

        if (loginAccount.isPresent()) {
            account = loginAccount.get();
            account.onActive();
        } else {
            account = AccountEntity.builder()
                    .thirdPartyAccessToken(kakaoOAuthLoginInfoDto.accessToken())
                    .thirdPartyRefreshToken(kakaoOAuthLoginInfoDto.refreshToken())
                    .oauthServiceId(kakaoOAuthLoginInfoDto.id())
                    .nickname(kakaoOAuthLoginInfoDto.nickname())
                    .isActive(true)
                    .build();
        }

        return account;
    }
}
