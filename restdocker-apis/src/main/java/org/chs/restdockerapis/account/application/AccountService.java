package org.chs.restdockerapis.account.application;

import com.auth0.jwt.interfaces.Claim;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.chs.domain.account.AccountRepository;
import org.chs.domain.account.entity.AccountEntity;
import org.chs.domain.common.enumerate.ThirdPartyEnum;
import org.chs.globalutils.dto.TokenDto;
import org.chs.restdockerapis.account.presentation.dto.ReIssueTokenRequest;
import org.chs.restdockerapis.account.presentation.dto.ReIssueTokenResponse;
import org.chs.restdockerapis.account.presentation.dto.common.GenericSingleResponse;
import org.chs.restdockerapis.account.presentation.dto.common.OAuthLoginRequestDto;
import org.chs.restdockerapis.account.presentation.dto.oauth.OAuthLoginInfoDto;
import org.chs.restdockerapis.account.util.kakao.KakaoOAuthUtils;
import org.chs.restdockerapis.account.util.naver.NaverOAuthUtils;
import org.chs.restdockerapis.common.argument_resolver.dto.GetRequesterDto;
import org.chs.restdockerapis.common.exception.*;
import org.chs.tokenissuer.application.TokenIssuerService;
import org.chs.tokenissuer.common.properties.JwtProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccountService {

    private final JwtProperties jwtProperties;

    private final KakaoOAuthUtils kakaoOAuthUtils;
    private final NaverOAuthUtils naverOAuthUtils;

    private final TokenIssuerService tokenIssuerService;
    private final AccountHistoryService accountHistoryService;

    private final AccountRepository accountRepository;

    private final String LOG_FORMAT_INFO_HISTORY = "\n[🔵INFO] - {}\n {}: {}";

    /**
     * @param ipAddress : 사용자 ip
     * @param request code : 사용자가 카카오 계정로그인을 동의하고 받은 인가코드
     * @return 회원가입 또는 로그인이 완료된 사용자의 정보를 바탕으로 만든 JWT (accessToken, refreshToken)
     *
     */
    @Transactional(rollbackFor = RestDockerException.class)
    public TokenDto kakaoOAuthLogin(String ipAddress, OAuthLoginRequestDto request) {
        OAuthLoginInfoDto kakaoOAuthLoginInfoDto = this.kakaoOAuthLoginWithExceptionHandling(ipAddress, request.code());

        AccountEntity account = createAccount(
                kakaoOAuthLoginInfoDto.id(),
                kakaoOAuthLoginInfoDto.nickname(),
                kakaoOAuthLoginInfoDto.accessToken(),
                kakaoOAuthLoginInfoDto.refreshToken(),
                ThirdPartyEnum.KAKAO
        );

        TokenDto oAuthLoginResponse = this.tokenIssueWithExceptionHandlingForKakao(
                ipAddress,
                account.getNickname(),
                account.getThirdPartyAccessToken(),
                account.getOauthServiceId()
        );
        account.setMyServiceToken(oAuthLoginResponse.accessToken(), oAuthLoginResponse.refreshToken());

        this.accountRepository.save(account);
        this.saveLoginHistoryWithExceptionHandling(account.getOauthServiceId(), ipAddress, false, null);

        return oAuthLoginResponse;
    }

    // History를 저장하려다 Exception이 발생되어 기존의 로직수행에 영향이 간다면 안된다. -> "History 저장로직과 메인로직은 독립적으로 운영되어야 함."
    // 따라서 Exception의 종류를 구별하여 rollback이 되지않게 Service 의 메인 트랜잭션 단에서 조절하고
    // 메인 로직이 실패했을 때도 로그인 실패 히스토리는 남아야되므로 독립적으로 실행할 수 있게 Propagation.NON_SUPPORTED 로 전파레벨을 설정한다.
    private OAuthLoginInfoDto kakaoOAuthLoginWithExceptionHandling(String ipAddress, String code) {
        try {
            return kakaoOAuthUtils.oAuthLogin(code);
        } catch (InternalServerException | CustomBadRequestException exception) {
            this.saveLoginHistoryWithExceptionHandling(null, ipAddress, true, exception.getMessage());
            throw exception;
        }
    }

    private TokenDto tokenIssueWithExceptionHandlingForKakao(String ipAddress, String nickname, String thirdPartyAccessToken, String oauthServiceId) {
        try {
            return tokenIssuerService.issueToken(oauthServiceId, nickname, ThirdPartyEnum.KAKAO.toString());
        } catch (InternalServerException | CustomBadRequestException exception) {
            // KaKao OAuth Logout을 한다.
            this.kakaoOAuthUtils.oAuthLogout(thirdPartyAccessToken, Long.valueOf(oauthServiceId));

            ErrorCode exceptionCause = ErrorCode.JWT_ISSUE_EXCEPTION;
            this.saveLoginHistoryWithExceptionHandling(null, ipAddress, true, exceptionCause.getDescription());
            throw new CustomTokenException(exceptionCause);
        }
    }

    /**
     * @param requesterInfo
     *    ipAddress : 사용자의 IP
     *    id : 사용자가 OAuth 를 통해 로그인했을 때, Third Party에서 제공해주는 Unique Id
     *    accessToken : RestDocker 서버에서 발급해주었던 AccessToken
     *
     * @return 로그아웃의 결과
     */
    @Transactional(rollbackFor = RestDockerException.class)
    public GenericSingleResponse<Boolean> kakaoOAuthLogout(GetRequesterDto requesterInfo) {
        boolean kakaoLogoutResult = kakaoOAuthLogoutWithExceptionHandling(requesterInfo.ipAddress(), requesterInfo.oauthAccessToken(), requesterInfo.id());

        if (!kakaoLogoutResult) {
            this.saveLogoutHistoryWithExceptionHandling(requesterInfo.ipAddress(), true, ErrorCode.KAKAO_LOGOUT_EXCEPTION.getDescription());
            return GenericSingleResponse.<Boolean>builder()
                    .data(false)
                    .build();
        }

        // 로그아웃 시, accessToken 과 refreshToken을 사용할 수 없게 만듬
        AccountEntity account = accountRepository.findByOauthServiceIdEqualsAndThirdPartyTypeEquals(requesterInfo.id(), ThirdPartyEnum.KAKAO)
                .orElseThrow(() -> new CustomTokenException(ErrorCode.ACCOUNT_NOT_EXIST_OAUTH_ID_EXCEPTION));

        account.eliminateValidToken();
        this.accountRepository.save(account);
        this.saveLogoutHistoryWithExceptionHandling(requesterInfo.ipAddress(), false, null);

        return GenericSingleResponse.<Boolean>builder()
                .data(true)
                .build();
    }

    private boolean kakaoOAuthLogoutWithExceptionHandling(String ipAddress, String thirdPartyAccessToken, String oauthServiceId) {
        try {
            return kakaoOAuthUtils.oAuthLogout(thirdPartyAccessToken, Long.valueOf(oauthServiceId));
        } catch (InternalServerException | CustomBadRequestException exception) {
            this.saveLogoutHistoryWithExceptionHandling(ipAddress, true, exception.getMessage());
            throw exception;
        }
    }

    /**
     * @param ipAddress : 사용자 ip
     * @param request code : 사용자가 네이버 계정로그인을 동의하고 받은 인가코드
     * @return 회원가입 또는 로그인이 완료된 사용자의 정보를 바탕으로 만든 JWT (accessToken, refreshToken)
     */
    @Transactional(rollbackFor = RestDockerException.class)
    public TokenDto naverOAuthLogin(String ipAddress, OAuthLoginRequestDto request) {
        OAuthLoginInfoDto naverOAuthLoginInfoDto = naverOAuthLoginWithExceptionHandling(ipAddress, request.code());

        AccountEntity account = createAccount(
                naverOAuthLoginInfoDto.id(),
                naverOAuthLoginInfoDto.nickname(),
                naverOAuthLoginInfoDto.accessToken(),
                naverOAuthLoginInfoDto.refreshToken(),
                ThirdPartyEnum.NAVER
        );

        TokenDto oAuthLoginResponse = tokenIssueWithExceptionHandlingForNaver(
                ipAddress,
                account.getNickname(),
                account.getThirdPartyAccessToken(),
                account.getOauthServiceId()
        );
        account.setMyServiceToken(oAuthLoginResponse.accessToken(), oAuthLoginResponse.refreshToken());

        this.accountRepository.save(account);
        this.saveLoginHistoryWithExceptionHandling(account.getOauthServiceId(), ipAddress, false,null);

        return oAuthLoginResponse;
    }

    private OAuthLoginInfoDto naverOAuthLoginWithExceptionHandling(String ipAddress, String code) {
        try {
            return naverOAuthUtils.oAuthLogin(code);
        } catch (InternalServerException | CustomBadRequestException exception) {
            this.saveLoginHistoryWithExceptionHandling(null, ipAddress, true, exception.getMessage());
            throw exception;
        }
    }

    private TokenDto tokenIssueWithExceptionHandlingForNaver(String ipAddress, String nickname, String thirdPartyAccessToken, String oauthServiceId) {
        try {
            return tokenIssuerService.issueToken(oauthServiceId, nickname, ThirdPartyEnum.NAVER.toString());
        } catch (Exception e) {
            this.naverOAuthUtils.oAuthLogout(thirdPartyAccessToken);

            ErrorCode exceptionCause = ErrorCode.JWT_ISSUE_EXCEPTION;
            this.saveLoginHistoryWithExceptionHandling(null, ipAddress, true, exceptionCause.getDescription());
            throw new CustomTokenException(exceptionCause);
        }
    }

    /**
     * @param requesterInfo
     *    ipAddress : 사용자의 IP
     *    id : 사용자가 OAuth 를 통해 로그인했을 때, Third Party에서 제공해주는 Unique Id
     *    accessToken : RestDocker 서버에서 발급해주었던 AccessToken
     *
     * @return 로그아웃의 결과
     */
    @Transactional(rollbackFor = RestDockerException.class)
    public GenericSingleResponse<Boolean> naverOAuthLogout(GetRequesterDto requesterInfo) {
        boolean naverLogoutResult = naverOAuthLogoutWithExceptionHandling(requesterInfo.ipAddress(), requesterInfo.oauthAccessToken());
        if (!naverLogoutResult) {
            this.saveLogoutHistoryWithExceptionHandling(requesterInfo.ipAddress(), true, ErrorCode.NAVER_LOGOUT_EXCEPTION.getDescription());

            return GenericSingleResponse.<Boolean>builder()
                    .data(false)
                    .build();
        }

        // 로그아웃 시, accessToken 과 refreshToken을 사용할 수 없게 만듬
        AccountEntity account = accountRepository.findByOauthServiceIdEqualsAndThirdPartyTypeEquals(requesterInfo.id(), ThirdPartyEnum.NAVER)
                .orElseThrow(() -> new CustomTokenException(ErrorCode.ACCOUNT_NOT_EXIST_OAUTH_ID_EXCEPTION));

        account.eliminateValidToken();
        accountRepository.save(account);
        this.saveLogoutHistoryWithExceptionHandling(requesterInfo.ipAddress(), false, null);

        return GenericSingleResponse.<Boolean>builder()
                .data(true)
                .build();
    }

    private boolean naverOAuthLogoutWithExceptionHandling(String ipAddress, String thirdPartyAccessToken) {
        try {
            return naverOAuthUtils.oAuthLogout(thirdPartyAccessToken);
        } catch (InternalServerException | CustomBadRequestException exception) {
            this.saveLogoutHistoryWithExceptionHandling(ipAddress, true, exception.getMessage());
            throw exception;
        }
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

    private void saveLoginHistoryWithExceptionHandling(String createdBy, String ipAddress, boolean failure, String failureReason) {
        try {
            // 히스토리 저장과는 관계없이 사용자에게 결과응답이 돼야하므로 ExceptionHandler 에서 제외
            this.accountHistoryService.saveLoginHistory(createdBy, ipAddress, failure,failureReason);
        } catch (Exception exception) {
            logInfoHistory(new HistoryException(ErrorCode.LOGIN_HISTORY_SAVE_EXCEPTION));
        }
    }

    private void saveLogoutHistoryWithExceptionHandling(String ipAddress, boolean failure, String failureReason) {
        try {
            this.accountHistoryService.saveLogoutHistory(ipAddress, failure,failureReason);
        } catch (Exception exception) {
            logInfoHistory(new HistoryException(ErrorCode.LOGOUT_HISTORY_SAVE_EXCEPTION));
        }
    }

    public GenericSingleResponse<String> naverStateValue() {
        return GenericSingleResponse.<String>builder()
                .data(naverOAuthUtils.naverStateValue())
                .build();
    }

    /**
     * @param request refreshToken : RestDocker 서버가 발급한 RefreshToken
     * @return accessToken : RestDocker 서버가 재발급해준 AccessToken
     */
    public ReIssueTokenResponse reIssueToken(ReIssueTokenRequest request) {
        Map<String, Claim> tokenClaims = tokenIssuerService.verifyRefreshToken(request.refreshToken());
        AccountEntity verifiedAccount = verifiedTokenClaims(tokenClaims, request.refreshToken());

        String reIssueAccessToken = tokenIssuerService.issueToken(
                verifiedAccount.getOauthServiceId(),
                verifiedAccount.getNickname(),
                verifiedAccount.getThirdPartyType().toString(),
                jwtProperties.getACCESS_TOKEN_EXPIRATION_TIME()
        );

        verifiedAccount.reIssueAccessToken(reIssueAccessToken);
        accountRepository.save(verifiedAccount);

        return ReIssueTokenResponse.builder()
                .accessToken(reIssueAccessToken)
                .build();
    }

    private AccountEntity verifiedTokenClaims(Map<String, Claim> tokenClaims, String requestRefreshToken) {
        String oauthServiceId = tokenClaims.get("oauthServiceId").asString();
        String thirdPartyType = tokenClaims.get("thirdPartyType").asString();
        if (null == oauthServiceId || null == thirdPartyType) {
            throw new CustomTokenException(ErrorCode.ACCOUNT_NOT_EXIST_OAUTH_ID_EXCEPTION);
        }

        Optional<AccountEntity> optionalAccount = accountRepository.findByOauthServiceIdEqualsAndThirdPartyTypeEquals(oauthServiceId, ThirdPartyEnum.valueOf(thirdPartyType));
        if (false == optionalAccount.isPresent()) {
            throw new CustomTokenException(ErrorCode.ACCOUNT_NOT_EXIST_OAUTH_ID_EXCEPTION);
        }

        AccountEntity verifiedAccount = optionalAccount.get();
        if (false == verifiedAccount.getRefreshToken().equals(requestRefreshToken)) {
            throw new CustomTokenException(ErrorCode.REFRESH_TOKEN_NOT_MATCH_INFO_EXCEPTION);
        }

        return verifiedAccount;
    }

    private void logInfoHistory(HistoryException exception) {
        log.info(LOG_FORMAT_INFO_HISTORY, exception.getErrorCode(), exception.getClass().getName(), exception.getErrorCode().getDescription());
    }

}
