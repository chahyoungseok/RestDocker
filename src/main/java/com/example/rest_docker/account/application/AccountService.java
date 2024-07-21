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
import com.example.rest_docker.common.exception.HistoryException;
import com.example.rest_docker.common.exception.HistoryExceptionCode;
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
    private final AccountHistoryService accountHistoryService;

    private final AccountRepository accountRepository;

    /**
     * @param ipAddress : 사용자 ip
     * @param request code : 사용자가 카카오 계정로그인을 동의하고 받은 인가코드
     * @return 회원가입 또는 로그인이 완료된 사용자의 정보를 바탕으로 만든 JWT (accessToken, refreshToken)
     * @throws RestDockerException (KAKAO_JSON_PROCESSING_EXCEPTION / KAKAO_JSON_MAPPING_EXCEPTION / HTTPCLIENT_ERROR_EXCEPTION)
     * @throws HistoryException (LOGIN_HISTORY_SAVE_EXCEPTION)
     *
     */
    @Transactional(noRollbackFor = HistoryException.class, rollbackFor = RestDockerException.class)
    public OAuthLoginResponse kakaoOAuthLogin(String ipAddress, OAuthLoginRequestDto request) throws RestDockerException, HistoryException {
        KakaoOAuthLoginInfoDto kakaoOAuthLoginInfoDto = this.kakaoOAuthLoginWithExceptionHandling(ipAddress, request.code());

        AccountEntity account = createAccount(
                kakaoOAuthLoginInfoDto.id(),
                kakaoOAuthLoginInfoDto.nickname(),
                kakaoOAuthLoginInfoDto.accessToken(),
                kakaoOAuthLoginInfoDto.refreshToken(),
                ThirdPartyEnum.KAKAO
        );

        OAuthLoginResponse oAuthLoginResponse = this.tokenIssueWithExceptionHandlingForKakao(
                ipAddress,
                account.getNickname(),
                account.getThirdPartyAccessToken(),
                account.getOauthServiceId()
        );
        account.setMyServiceToken(oAuthLoginResponse);

        this.accountRepository.saveAndFlush(account);
        this.saveLoginHistoryWithExceptionHandling(account.getOauthServiceId(), ipAddress, false, null);

        return oAuthLoginResponse;
    }

    // History를 저장하려다 Exception이 발생되어 기존의 로직수행에 영향이 간다면 안된다. -> "History 저장로직과 메인로직은 독립적으로 운영되어야 함."
    // 따라서 Exception의 종류를 구별하여 rollback이 되지않게 Service 의 메인 트랜잭션 단에서 조절하고
    // 메인 로직이 실패했을 때도 로그인 실패 히스토리는 남아야되므로 독립적으로 실행할 수 있게 Propagation.NON_SUPPORTED 로 전파레벨을 설정한다.
    private KakaoOAuthLoginInfoDto kakaoOAuthLoginWithExceptionHandling(String ipAddress, String code) throws RestDockerException, HistoryException {
        try {
            return kakaoOAuthUtils.kakaoOAuthLogin(code);
        } catch (RestDockerException exception) {
            this.saveLoginHistoryWithExceptionHandling(null, ipAddress, true, exception.getExceptionCode().getDescription());
            throw exception;
        }
    }

    private OAuthLoginResponse tokenIssueWithExceptionHandlingForKakao(String ipAddress, String nickname, String thirdPartyAccessToken, String oauthServiceId) throws RestDockerException, HistoryException {
        try {
            return tokenIssuerService.issueToken(oauthServiceId, nickname, ThirdPartyEnum.KAKAO);
        } catch (Exception e) {
            // KaKao OAuth Logout을 한다.
            this.kakaoOAuthUtils.kakaoOAuthLogout(thirdPartyAccessToken, Long.valueOf(oauthServiceId));

            RestDockerExceptionCode exceptionCause = RestDockerExceptionCode.JWT_ISSUE_EXCEPTION;
            this.saveLoginHistoryWithExceptionHandling(null, ipAddress, true, exceptionCause.getDescription());
            throw new RestDockerException(exceptionCause);
        }
    }

    /**
     * @param requesterInfo
     *    ipAddress : 사용자의 IP
     *    id : 사용자가 OAuth 를 통해 로그인했을 때, Third Party에서 제공해주는 Unique Id
     *    accessToken : RestDocker 서버에서 발급해주었던 AccessToken
     *
     * @return 로그아웃의 결과
     * @throws RestDockerException (HTTPCLIENT_ERROR_EXCEPTION / KAKAO_JSON_PROCESSING_EXCEPTION / KAKAO_LOGOUT_EXCEPTION / ACCOUNT_NOT_EXIST_OAUTH_ID_EXCEPTION)
     * @throws HistoryException (LOGOUT_HISTORY_SAVE_EXCEPTION)
     */
    @Transactional(rollbackFor = RestDockerException.class)
    public boolean kakaoOAuthLogout(GetRequesterDto requesterInfo) throws RestDockerException, HistoryException {
        boolean kakaoLogoutResult = kakaoOAuthLogoutWithExceptionHandling(requesterInfo.ipAddress(), requesterInfo.oauthAccessToken(), requesterInfo.id());

        if (!kakaoLogoutResult) {
            RestDockerException exception = new RestDockerException(RestDockerExceptionCode.KAKAO_LOGOUT_EXCEPTION);
            this.saveLogoutHistoryWithExceptionHandling(requesterInfo.ipAddress(), true, exception.getExceptionCode().getDescription());
            throw exception;
        }

        // 로그아웃 시, accessToken 과 refreshToken을 사용할 수 없게 만듬
        AccountEntity account = accountRepository.findByOauthServiceIdEqualsAndThirdPartyTypeEquals(requesterInfo.id(), ThirdPartyEnum.KAKAO)
                .orElseThrow(() -> new RestDockerException(RestDockerExceptionCode.ACCOUNT_NOT_EXIST_OAUTH_ID_EXCEPTION));

        account.eliminateValidToken();
        this.accountRepository.save(account);
        this.accountHistoryService.saveLogoutHistory(requesterInfo.ipAddress(), false, null);

        return true;
    }

    private boolean kakaoOAuthLogoutWithExceptionHandling(String ipAddress, String thirdPartyAccessToken, String oauthServiceId) throws RestDockerException, HistoryException {
        try {
            return kakaoOAuthUtils.kakaoOAuthLogout(thirdPartyAccessToken, Long.valueOf(oauthServiceId));
        } catch (RestDockerException exception) {
            this.saveLogoutHistoryWithExceptionHandling(ipAddress, true, exception.getExceptionCode().getDescription());
            throw exception;
        }
    }

    /**
     * @param ipAddress : 사용자 ip
     * @param request code : 사용자가 네이버 계정로그인을 동의하고 받은 인가코드
     * @return 회원가입 또는 로그인이 완료된 사용자의 정보를 바탕으로 만든 JWT (accessToken, refreshToken)
     * @throws RestDockerException (NAVER_JSON_PROCESSING_EXCEPTION / NAVER_JSON_MAPPING_EXCEPTION / HTTPCLIENT_ERROR_EXCEPTION)
     * @throws HistoryException (LOGIN_HISTORY_SAVE_EXCEPTION)
     */
    @Transactional(noRollbackFor = HistoryException.class, rollbackFor = RestDockerException.class)
    public OAuthLoginResponse naverOAuthLogin(String ipAddress, OAuthLoginRequestDto request) throws RestDockerException, HistoryException {
        NaverOAuthLoginInfoDto naverOAuthLoginInfoDto = naverOAuthLoginWithExceptionHandling(ipAddress, request.code());

        AccountEntity account = createAccount(
                naverOAuthLoginInfoDto.id(),
                naverOAuthLoginInfoDto.nickname(),
                naverOAuthLoginInfoDto.accessToken(),
                naverOAuthLoginInfoDto.refreshToken(),
                ThirdPartyEnum.NAVER
        );

        OAuthLoginResponse oAuthLoginResponse = tokenIssueWithExceptionHandlingForNaver(
                ipAddress,
                account.getNickname(),
                account.getThirdPartyAccessToken(),
                account.getOauthServiceId()
        );
        account.setMyServiceToken(oAuthLoginResponse);

        this.accountRepository.save(account);
        this.saveLoginHistoryWithExceptionHandling(account.getOauthServiceId(), ipAddress, false,null);

        return oAuthLoginResponse;
    }

    private NaverOAuthLoginInfoDto naverOAuthLoginWithExceptionHandling(String ipAddress, String code) throws RestDockerException, HistoryException {
        try {
            return naverOAuthUtils.naverOAuthLogin(code);
        } catch (RestDockerException exception) {
            this.saveLoginHistoryWithExceptionHandling(null, ipAddress, true, exception.getExceptionCode().getDescription());
            throw exception;
        }
    }

    private OAuthLoginResponse tokenIssueWithExceptionHandlingForNaver(String ipAddress, String nickname, String thirdPartyAccessToken, String oauthServiceId) throws RestDockerException, HistoryException {
        try {
            return tokenIssuerService.issueToken(oauthServiceId, nickname, ThirdPartyEnum.NAVER);
        } catch (Exception e) {
            this.naverOAuthUtils.naverOAuthLogout(thirdPartyAccessToken);

            RestDockerExceptionCode exceptionCause = RestDockerExceptionCode.JWT_ISSUE_EXCEPTION;
            this.saveLoginHistoryWithExceptionHandling(null, ipAddress, true, exceptionCause.getDescription());
            throw new RestDockerException(exceptionCause);
        }
    }

    /**
     * @param requesterInfo
     *    ipAddress : 사용자의 IP
     *    id : 사용자가 OAuth 를 통해 로그인했을 때, Third Party에서 제공해주는 Unique Id
     *    accessToken : RestDocker 서버에서 발급해주었던 AccessToken
     *
     * @return 로그아웃의 결과
     * @throws RestDockerException (HTTPCLIENT_ERROR_EXCEPTION / NAVER_JSON_PROCESSING_EXCEPTION / NAVER_LOGOUT_EXCEPTION / ACCOUNT_NOT_EXIST_OAUTH_ID_EXCEPTION)
     * @throws HistoryException (LOGOUT_HISTORY_SAVE_EXCEPTION)
     */
    @Transactional(noRollbackFor = HistoryException.class, rollbackFor = RestDockerException.class)
    public Object naverOAuthLogout(GetRequesterDto requesterInfo) throws RestDockerException, HistoryException {
        boolean naverLogoutResult = naverOAuthLogoutWithExceptionHandling(requesterInfo.ipAddress(), requesterInfo.oauthAccessToken());
        if (!naverLogoutResult) {
            RestDockerException exception = new RestDockerException(RestDockerExceptionCode.NAVER_LOGOUT_EXCEPTION);
            this.saveLogoutHistoryWithExceptionHandling(requesterInfo.ipAddress(), true, exception.getExceptionCode().getDescription());
            throw exception;
        }

        // 로그아웃 시, accessToken 과 refreshToken을 사용할 수 없게 만듬
        AccountEntity account = accountRepository.findByOauthServiceIdEqualsAndThirdPartyTypeEquals(requesterInfo.id(), ThirdPartyEnum.NAVER)
                .orElseThrow(() -> new RestDockerException(RestDockerExceptionCode.ACCOUNT_NOT_EXIST_OAUTH_ID_EXCEPTION));

        account.eliminateValidToken();
        accountRepository.save(account);
        this.accountHistoryService.saveLogoutHistory(requesterInfo.ipAddress(), false, null);

        return true;
    }

    private boolean naverOAuthLogoutWithExceptionHandling(String ipAddress, String thirdPartyAccessToken) throws RestDockerException, HistoryException {
        try {
            return naverOAuthUtils.naverOAuthLogout(thirdPartyAccessToken);
        } catch (RestDockerException exception) {
            this.saveLogoutHistoryWithExceptionHandling(ipAddress, true, exception.getExceptionCode().getDescription());
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

    private void saveLoginHistoryWithExceptionHandling(String createdBy, String ipAddress, boolean failure, String failureReason) throws HistoryException {
        try {
            // 히스토리 저장과는 관계없이 사용자에게 결과응답이 돼야하므로 ExceptionHandler 에서 제외
            this.accountHistoryService.saveLoginHistory(createdBy, ipAddress, failure,failureReason);
        } catch (Exception exception) {
            throw new HistoryException(HistoryExceptionCode.LOGIN_HISTORY_SAVE_EXCEPTION);
        }
    }

    private void saveLogoutHistoryWithExceptionHandling(String ipAddress, boolean failure, String failureReason) throws HistoryException {
        try {
            this.accountHistoryService.saveLogoutHistory(ipAddress, failure,failureReason);
        } catch (Exception exception) {
            throw new HistoryException(HistoryExceptionCode.LOGOUT_HISTORY_SAVE_EXCEPTION);
        }
    }

    public String naverStateValue() {
        return naverOAuthUtils.naverStateValue();
    }
}
