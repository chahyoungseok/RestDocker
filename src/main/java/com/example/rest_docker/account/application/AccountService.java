package com.example.rest_docker.account.application;

import com.example.rest_docker.account.domain.AccountRepository;
import com.example.rest_docker.account.domain.entity.AccountEntity;
import com.example.rest_docker.account.presentation.dto.kakao.KakaoOAuthLoginInfoDto;
import com.example.rest_docker.account.presentation.dto.OAuthLoginRequestDto;
import com.example.rest_docker.account.presentation.dto.OAuthLoginResponse;
import com.example.rest_docker.account.presentation.dto.naver.NaverOAuthLoginInfoDto;
import com.example.rest_docker.account.util.kakao.KakaoOAuthUtils;
import com.example.rest_docker.account.util.naver.NaverOAuthUtils;
import com.example.rest_docker.common.argument_resolver.dto.GetRequesterDto;
import com.example.rest_docker.common.enumerate.ThirdPartyEnum;
import com.example.rest_docker.common.exception.RestDockerException;
import com.example.rest_docker.common.exception.RestDockerExceptionCode;
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
    private final NaverOAuthUtils naverOAuthUtils;

    private final TokenIssuerService tokenIssuerService;

    private final AccountRepository accountRepository;

    /**
     * @param request
     *    code : 사용자가 카카오 계정로그인을 동의하고 받은 인가코드
     * @return 회원가입 또는 로그인이 완료된 사용자의 정보를 바탕으로 만든 JWT (accessToken, refreshToken)
     * @throws RestDockerException (KAKAO_JSON_PROCESSING_EXCEPTION / KAKAO_JSON_MAPPING_EXCEPTION / HTTPCLIENT_ERROR_EXCEPTION)
     */
    @Transactional(rollbackFor = RestDockerException.class)
    public OAuthLoginResponse kakaoOAuthLogin(OAuthLoginRequestDto request) throws RestDockerException {
        KakaoOAuthLoginInfoDto kakaoOAuthLoginInfoDto = kakaoOAuthUtils.kakaoOAuthLogin(request.code());
        AccountEntity account = createAccount(
                kakaoOAuthLoginInfoDto.id(),
                kakaoOAuthLoginInfoDto.nickname(),
                kakaoOAuthLoginInfoDto.accessToken(),
                kakaoOAuthLoginInfoDto.refreshToken(),
                ThirdPartyEnum.KAKAO
        );

        OAuthLoginResponse oAuthLoginResponse = null;
        try {
            oAuthLoginResponse = tokenIssuerService.issueToken(account.getOauthServiceId(), account.getNickname(), ThirdPartyEnum.KAKAO);
            account.setMyServiceToken(oAuthLoginResponse);
        } catch (Exception e) {
            // KaKao OAuth Logout을 한다.
            this.kakaoOAuthUtils.kakaoOAuthLogout(kakaoOAuthLoginInfoDto.accessToken(), Long.valueOf(kakaoOAuthLoginInfoDto.id()));
        }

        accountRepository.save(account);
        return oAuthLoginResponse;
    }

    /**
     * @param requesterInfo
     *    ipAddress : 사용자의 IP
     *    id : 사용자가 OAuth 를 통해 로그인했을 때, Third Party에서 제공해주는 Unique Id
     *    accessToken : RestDocker 서버에서 발급해주었던 AccessToken
     *
     * @return 로그아웃의 결과
     * @throws RestDockerException (HTTPCLIENT_ERROR_EXCEPTION / KAKAO_JSON_PROCESSING_EXCEPTION / KAKAO_LOGOUT_EXCEPTION / ACCOUNT_NOT_EXIST_OAUTH_ID_EXCEPTION)
     */
    @Transactional(rollbackFor = RestDockerException.class)
    public boolean kakaoOAuthLogout(GetRequesterDto requesterInfo) throws RestDockerException {
        boolean result = kakaoOAuthUtils.kakaoOAuthLogout(requesterInfo.oauthAccessToken(), Long.valueOf(requesterInfo.id()));
        if (!result) {
            throw new RestDockerException(RestDockerExceptionCode.KAKAO_LOGOUT_EXCEPTION);
        }

        // 로그아웃 시, accessToken 과 refreshToken을 사용할 수 없게 만듬
        AccountEntity account = accountRepository.findByOauthServiceIdEqualsAndThirdPartyTypeEquals(requesterInfo.id(), ThirdPartyEnum.KAKAO)
                .orElseThrow(() -> new RestDockerException(RestDockerExceptionCode.ACCOUNT_NOT_EXIST_OAUTH_ID_EXCEPTION));

        account.eliminateValidToken();
        accountRepository.save(account);

        return true;
    }

    /**
     * @param request
     *    code : 사용자가 네이버 계정로그인을 동의하고 받은 인가코드
     * @return 회원가입 또는 로그인이 완료된 사용자의 정보를 바탕으로 만든 JWT (accessToken, refreshToken)
     * @throws RestDockerException (NAVER_JSON_PROCESSING_EXCEPTION / NAVER_JSON_MAPPING_EXCEPTION / HTTPCLIENT_ERROR_EXCEPTION)
     */
    @Transactional(rollbackFor = RestDockerException.class)
    public OAuthLoginResponse naverOAuthLogin(OAuthLoginRequestDto request) throws RestDockerException {
        NaverOAuthLoginInfoDto naverOAuthLoginInfoDto = naverOAuthUtils.naverOAuthLogin(request.code());
        AccountEntity account = createAccount(
                naverOAuthLoginInfoDto.id(),
                naverOAuthLoginInfoDto.nickname(),
                naverOAuthLoginInfoDto.accessToken(),
                naverOAuthLoginInfoDto.refreshToken(),
                ThirdPartyEnum.NAVER
        );

        OAuthLoginResponse oAuthLoginResponse = null;
        try {
            oAuthLoginResponse = tokenIssuerService.issueToken(account.getOauthServiceId(), account.getNickname(), ThirdPartyEnum.NAVER);
            account.setMyServiceToken(oAuthLoginResponse);
        } catch (Exception e) {
            // KaKao OAuth Logout을 한다.
            this.naverOAuthUtils.naverOAuthLogout(naverOAuthLoginInfoDto.accessToken());
        }

        accountRepository.save(account);
        return oAuthLoginResponse;
    }

    /**
     * @param requesterInfo
     *    ipAddress : 사용자의 IP
     *    id : 사용자가 OAuth 를 통해 로그인했을 때, Third Party에서 제공해주는 Unique Id
     *    accessToken : RestDocker 서버에서 발급해주었던 AccessToken
     *
     * @return 로그아웃의 결과
     * @throws RestDockerException (HTTPCLIENT_ERROR_EXCEPTION / NAVER_JSON_PROCESSING_EXCEPTION / NAVER_LOGOUT_EXCEPTION / ACCOUNT_NOT_EXIST_OAUTH_ID_EXCEPTION)
     */
    @Transactional(rollbackFor = RestDockerException.class)
    public Object naverOAuthLogout(GetRequesterDto requesterInfo) throws RestDockerException {
        boolean result = naverOAuthUtils.naverOAuthLogout(requesterInfo.oauthAccessToken());
        if (!result) {
            throw new RestDockerException(RestDockerExceptionCode.NAVER_LOGOUT_EXCEPTION);
        }

        // 로그아웃 시, accessToken 과 refreshToken을 사용할 수 없게 만듬
        AccountEntity account = accountRepository.findByOauthServiceIdEqualsAndThirdPartyTypeEquals(requesterInfo.id(), ThirdPartyEnum.NAVER)
                .orElseThrow(() -> new RestDockerException(RestDockerExceptionCode.ACCOUNT_NOT_EXIST_OAUTH_ID_EXCEPTION));

        account.eliminateValidToken();
        accountRepository.save(account);

        return true;
    }


    private AccountEntity createAccount(String id, String nickname, String oauthAccessToken, String oauthRefreshToken, ThirdPartyEnum thirdPartyType) {
        Optional<AccountEntity> loginAccount = accountRepository.findByOauthServiceIdEqualsAndThirdPartyTypeEquals(id, thirdPartyType);

        // 1. 로그인이 처음인 경우
        // 2. 회원가입은 되어있지만 활성화되어있지 않은경우
        // 3. 회원가입은 되어있고 활성화도 되어있는 경우

        AccountEntity account = null;

        if (loginAccount.isPresent()) {
            account = loginAccount.get();
            account.onActive();
        } else {
            account = AccountEntity.builder()
                    .thirdPartyAccessToken(oauthAccessToken)
                    .thirdPartyRefreshToken(oauthRefreshToken)
                    .oauthServiceId(id)
                    .nickname(nickname)
                    .thirdPartyType(thirdPartyType)
                    .isActive(true)
                    .build();
        }

        return account;
    }

    public String naverStateValue() {
        return naverOAuthUtils.naverStateValue();
    }
}
