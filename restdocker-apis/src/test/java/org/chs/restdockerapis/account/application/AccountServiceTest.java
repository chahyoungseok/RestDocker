package org.chs.restdockerapis.account.application;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import org.chs.domain.account.AccountRepository;
import org.chs.domain.account.entity.AccountEntity;
import org.chs.domain.common.enumerate.ThirdPartyEnum;
import org.chs.domain.network.NetworkEntityRepository;
import org.chs.domain.network.entity.NetworkEntity;
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
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @InjectMocks
    private AccountService accountService;

    @Mock
    private TokenIssuerService tokenIssuerService;

    @Mock
    private AccountHistoryService accountHistoryService;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private NetworkEntityRepository dockerNetworkEntityRepository;

    @Mock
    private KakaoOAuthUtils kakaoOAuthUtils;

    @Mock
    private NaverOAuthUtils naverOAuthUtils;

    @Mock
    private JwtProperties jwtProperties;

    // 주관적 정의 : 시나리오 테스트란 하나의 메서드를 여러 시나리오에 맞춰 테스트 하는 것을 정의하였다.
    @Nested
    @DisplayName("[Account][시나리오 테스트] 카카오 OAuth 로그인을 테스트한다.")
    class KakaoOAuthLogin {

        private String testIpAddress = null;

        private TokenDto testTokenDto = null;
        private OAuthLoginRequestDto testRequest = null;
        private OAuthLoginInfoDto testOAuthLoginInfoDto = null;
        private AccountEntity testAccount = null;
        private NetworkEntity testNetwork = null;


        protected KakaoOAuthLogin() {
            // given - data
            testOAuthLoginInfoDto = OAuthLoginInfoDto.builder()
                    .id("testId")
                    .nickname("testNickname")
                    .accessToken("testAccessToken")
                    .refreshToken("testRefreshToken")
                    .build();

            testRequest = OAuthLoginRequestDto.builder()
                    .code("Success Code")
                    .build();

            testTokenDto = TokenDto.builder()
                    .accessToken("testIssuedAccessToken")
                    .refreshToken("testIssuedRefreshToken")
                    .build();

            testAccount = AccountEntity.builder()
                    .nickname("테스트용 계정1")
                    .oauthServiceId("test_account_1")
                    .thirdPartyType(ThirdPartyEnum.KAKAO)
                    .isActive(true)
                    .build();

            testIpAddress = "IP";

            testNetwork = NetworkEntity.builder()
                    .account(testAccount)
                    .name("testName")
                    .subnet("testSubnet")
                    .mtu(1000)
                    .enableIcc(true)
                    .build();
        }

        @Tag("business")
        @Test
        @DisplayName("[Account][Business] 카카오 로그인 후 토큰발급을 정상적으로 성공한다")
        void 카카오_로그인_후_토큰발급을_정상적으로_성공한다() {
            // given - mocking
            BDDMockito.given(kakaoOAuthUtils.oAuthLogin(any()))
                    .willReturn(testOAuthLoginInfoDto);

            BDDMockito.given(tokenIssuerService.issueToken(any(), any(), any()))
                    .willReturn(testTokenDto);

            BDDMockito.given(accountRepository.save(any()))
                    .willReturn(testAccount);

            BDDMockito.willDoNothing().given(accountHistoryService)
                    .saveLoginHistory(any(), any(), anyBoolean(), any());

            BDDMockito.given(dockerNetworkEntityRepository.save(any()))
                    .willReturn(testNetwork);

            // when
            TokenDto actual = accountService.kakaoOAuthLogin(testIpAddress, testRequest);

            // then
            Assertions.assertEquals(testTokenDto, actual);
        }

        @Tag("business")
        @Test
        @DisplayName("[Account][Business] 카카오 로그인시 통신과정에서 서버의 원인으로 실패한다")
        void 카카오_로그인시_통신과정에서_서버의_원인으로_실패한다() {
            // given - mocking
            BDDMockito.given(kakaoOAuthUtils.oAuthLogin(any()))
                    .willThrow(new InternalServerException(ErrorCode.THIRD_PARTY_AUTHORIZATION_SERVER_EXCEPTION));

            BDDMockito.willDoNothing().given(accountHistoryService)
                    .saveLoginHistory(any(), any(), anyBoolean(), any());

            // when && then
            Assertions.assertThrows(
                    InternalServerException.class,
                    () -> accountService.kakaoOAuthLogin(testIpAddress, testRequest)
            );
        }

        @Tag("business")
        @Test
        @DisplayName("[Account][Business] 카카오 로그인시 통신과정에서 클라이언트의 원인으로 실패한다")
        void 카카오_로그인시_통신과정에서_클라이언트의_원인으로_실패한다() {
            // given - mocking
            BDDMockito.given(kakaoOAuthUtils.oAuthLogin(any()))
                    .willThrow(new CustomBadRequestException(ErrorCode.THIRD_PARTY_CLIENT_EXCEPTION));

            BDDMockito.willDoNothing().given(accountHistoryService)
                    .saveLoginHistory(any(), any(), anyBoolean(), any());

            // when && then
            Assertions.assertThrows(
                    CustomBadRequestException.class,
                    () -> accountService.kakaoOAuthLogin(testIpAddress, testRequest)
            );
        }

        @Tag("business")
        @Test
        @DisplayName("[Account][Business] 카카오 로그인시 로그인 실패 저장예외가 발생해도 비즈니스Exception인, 통신과정 Exception이 발생하도록한다")
        void 카카오_로그인시_로그인_실패_저장예외가_발생해도_비즈니스Exception인_통신과정Exception이_발생하도록한다() {
            // given - mocking
            BDDMockito.given(kakaoOAuthUtils.oAuthLogin(any()))
                    .willThrow(new InternalServerException(ErrorCode.THIRD_PARTY_AUTHORIZATION_SERVER_EXCEPTION));

            BDDMockito.willThrow(new HistoryException(ErrorCode.LOGIN_HISTORY_SAVE_EXCEPTION)).given(accountHistoryService)
                    .saveLoginHistory(any(), any(), anyBoolean(), any());

            // when && then
            Assertions.assertThrows(
                    InternalServerException.class,
                    () -> accountService.kakaoOAuthLogin(testIpAddress, testRequest)
            );
        }

        @Tag("business")
        @Test
        @DisplayName("[Account][Business] 카카오 로그인 후 토큰발급에서 Exception이 발생한다.")
        void 카카오_로그인_후_토큰발급에서_Exception이_발생한다() {
            // given - mocking
            BDDMockito.given(kakaoOAuthUtils.oAuthLogin(any()))
                    .willReturn(testOAuthLoginInfoDto);

            BDDMockito.given(tokenIssuerService.issueToken(any(), any(), any()))
                    .willThrow(new CustomTokenException(ErrorCode.JWT_ISSUE_EXCEPTION));

            // when && then
            Assertions.assertThrows(
                    CustomTokenException.class,
                    () -> accountService.kakaoOAuthLogin(testIpAddress, testRequest)
            );
        }

        @Tag("business")
        @Test
        @DisplayName("[Account][Business] 카카오 로그인 후 히스토리 Exception이 발생해도 클라이언트에 정상로그인 응답을준다")
        void 카카오_로그인_후_히스토리Exception이_발생해도_클라이언트에_정상로그인_응답을준다() {
            // given - mocking
            BDDMockito.given(kakaoOAuthUtils.oAuthLogin(any()))
                    .willReturn(testOAuthLoginInfoDto);

            BDDMockito.given(tokenIssuerService.issueToken(any(), any(), any()))
                    .willReturn(testTokenDto);

            BDDMockito.given(accountRepository.save(any()))
                    .willReturn(testAccount);

            BDDMockito.given(dockerNetworkEntityRepository.save(any()))
                    .willReturn(testNetwork);

            BDDMockito.willThrow(new HistoryException(ErrorCode.LOGIN_HISTORY_SAVE_EXCEPTION)).given(accountHistoryService)
                    .saveLoginHistory(any(), any(), anyBoolean(), any());

            // when
            TokenDto actual = accountService.kakaoOAuthLogin(testIpAddress, testRequest);

            // then
            Assertions.assertEquals(testTokenDto, actual);
        }
    }

    @Nested
    @DisplayName("[Account][시나리오 테스트] 카카오 OAuth 로그아웃을 테스트한다.")
    class KakaoOAuthLogout {

        private GetRequesterDto testRequesterInfo = null;
        private AccountEntity testAccount = null;
        private GenericSingleResponse<Boolean> testSuccessResponse = null;
        private GenericSingleResponse<Boolean> testFailResponse = null;


        protected KakaoOAuthLogout() {
            // given - data
            testRequesterInfo = GetRequesterDto.builder()
                    .id("1")
                    .ipAddress("testIpAddress")
                    .oauthAccessToken("testOAuthAccessToken")
                    .build();

            testAccount = AccountEntity.builder()
                    .nickname("테스트용 계정1")
                    .oauthServiceId("test_account_1")
                    .thirdPartyType(ThirdPartyEnum.KAKAO)
                    .isActive(true)
                    .build();

            testSuccessResponse = GenericSingleResponse.<Boolean>builder()
                    .data(true)
                    .build();

            testFailResponse = GenericSingleResponse.<Boolean>builder()
                    .data(false)
                    .build();
        }

        @Tag("business")
        @Test
        @DisplayName("[Account][Business] 카카오 로그아웃을 정상적으로 성공한다")
        void 카카오_로그아웃을_정상적으로_성공한다() {
            // given - mocking
            BDDMockito.given(kakaoOAuthUtils.oAuthLogout(any(), anyLong()))
                    .willReturn(true);

            BDDMockito.given(accountRepository.findByOauthServiceIdEqualsAndThirdPartyTypeEquals(any(), any()))
                    .willReturn(Optional.of(testAccount));

            BDDMockito.willDoNothing().given(accountHistoryService)
                    .saveLogoutHistory(any(), anyBoolean(), any());

            BDDMockito.given(accountRepository.save(any()))
                    .willReturn(testAccount);

            // when
            GenericSingleResponse<Boolean> actual = accountService.kakaoOAuthLogout(testRequesterInfo);

            // then
            Assertions.assertEquals(testSuccessResponse.data(), actual.data());
        }

        @Tag("business")
        @Test
        @DisplayName("[Account][Business] 카카오 로그아웃을 실패한다")
        void 카카오_로그아웃을_실패한다() {
            // given - mocking
            BDDMockito.given(kakaoOAuthUtils.oAuthLogout(any(), anyLong()))
                    .willReturn(false);

            BDDMockito.willDoNothing().given(accountHistoryService)
                    .saveLogoutHistory(any(), anyBoolean(), any());

            // when
            GenericSingleResponse<Boolean> actual = accountService.kakaoOAuthLogout(testRequesterInfo);

            // then
            Assertions.assertEquals(testFailResponse.data(), actual.data());
        }

        @Tag("business")
        @Test
        @DisplayName("[Account][Business] 카카오 로그아웃시, 서버의 원인으로 실패한다")
        void 카카오_로그아웃시_서버의_원인으로_실패한다() {
            // given - mocking
            BDDMockito.given(kakaoOAuthUtils.oAuthLogout(any(), anyLong()))
                    .willThrow(new InternalServerException(ErrorCode.KAKAO_LOGOUT_EXCEPTION));

            BDDMockito.willDoNothing().given(accountHistoryService)
                    .saveLogoutHistory(any(), anyBoolean(), any());

            // when && then
            Assertions.assertThrows(
                    InternalServerException.class,
                    () -> accountService.kakaoOAuthLogout(testRequesterInfo)
            );
        }

        @Tag("business")
        @Test
        @DisplayName("[Account][Business] 카카오 로그아웃시, 클라이언트의 원인으로 실패한다")
        void 카카오_로그아웃시_클라이언트의_원인으로_실패한다() {
            // given - mocking
            BDDMockito.given(kakaoOAuthUtils.oAuthLogout(any(), anyLong()))
                    .willThrow(new CustomBadRequestException(ErrorCode.THIRD_PARTY_CLIENT_EXCEPTION));

            BDDMockito.willDoNothing().given(accountHistoryService)
                    .saveLogoutHistory(any(), anyBoolean(), any());

            // when && then
            Assertions.assertThrows(
                    CustomBadRequestException.class,
                    () -> accountService.kakaoOAuthLogout(testRequesterInfo)
            );
        }

        @Tag("business")
        @Test
        @DisplayName("[Account][Business] 카카오 로그아웃시 로그아웃 실패 저장예외가 발생해도 비즈니스Exception인, 통신과정 Exception이 발생하도록한다")
        void 카카오_로그아웃시_로그인_실패_저장예외가_발생해도_비즈니스Exception인_통신과정Exception이_발생하도록한다() {
            // given - mocking
            BDDMockito.given(kakaoOAuthUtils.oAuthLogout(any(), anyLong()))
                    .willThrow(new InternalServerException(ErrorCode.THIRD_PARTY_AUTHORIZATION_SERVER_EXCEPTION));

            BDDMockito.willThrow(new HistoryException(ErrorCode.LOGOUT_HISTORY_SAVE_EXCEPTION)).given(accountHistoryService)
                    .saveLogoutHistory(any(), anyBoolean(), any());

            // when && then
            Assertions.assertThrows(
                    InternalServerException.class,
                    () -> accountService.kakaoOAuthLogout(testRequesterInfo)
            );
        }

        @Tag("business")
        @Test
        @DisplayName("[Account][Business] 카카오 로그아웃시 Token 안에 같이 온 AccountId가 DB에 없는 경우Exception이 발생하도록한다")
        void 카카오_로그아웃시_Token안에_같이_온_AccountId가_DB에_없는_경우_Exception이_발생하도록한다() {
            // given - mocking
            BDDMockito.given(kakaoOAuthUtils.oAuthLogout(any(), anyLong()))
                    .willReturn(true);

            BDDMockito.willThrow(new CustomTokenException(ErrorCode.ACCOUNT_NOT_EXIST_OAUTH_ID_EXCEPTION)).given(accountRepository)
                    .findByOauthServiceIdEqualsAndThirdPartyTypeEquals(any(), any());

            // when && then
            Assertions.assertThrows(
                    CustomTokenException.class,
                    () -> accountService.kakaoOAuthLogout(testRequesterInfo)
            );
        }

        @Tag("business")
        @Test
        @DisplayName("[Account][Business] 카카오 로그아웃 후 히스토리 Exception이 발생해도 클라이언트에 정상로그아웃 응답을준다")
        void 카카오_로그아웃_후_히스토리Exception이_발생해도_클라이언트에_정상로그아웃_응답을준다() {
            // given - mocking
            BDDMockito.given(kakaoOAuthUtils.oAuthLogout(any(), anyLong()))
                    .willReturn(true);

            BDDMockito.given(accountRepository.findByOauthServiceIdEqualsAndThirdPartyTypeEquals(any(), any()))
                    .willReturn(Optional.of(testAccount));

            BDDMockito.given(accountRepository.save(any()))
                    .willReturn(testAccount);

            BDDMockito.willThrow(new HistoryException(ErrorCode.LOGOUT_HISTORY_SAVE_EXCEPTION)).given(accountHistoryService)
                    .saveLogoutHistory(any(), anyBoolean(), any());

            // when
            GenericSingleResponse<Boolean> actual = accountService.kakaoOAuthLogout(testRequesterInfo);

            // then
            Assertions.assertEquals(testSuccessResponse.data(), actual.data());
        }
    }

    @Nested
    @DisplayName("[Account][시나리오 테스트] 네이버 OAuth 로그인을 테스트한다.")
    class NaverOAuthLogin {

        private String testIpAddress = null;

        private TokenDto testTokenDto = null;
        private OAuthLoginRequestDto testRequest = null;
        private OAuthLoginInfoDto testOAuthLoginInfoDto = null;
        private AccountEntity testAccount = null;
        private NetworkEntity testNetwork = null;


        protected NaverOAuthLogin() {
            // given - data
            testOAuthLoginInfoDto = OAuthLoginInfoDto.builder()
                    .id("testId")
                    .nickname("testNickname")
                    .accessToken("testAccessToken")
                    .refreshToken("testRefreshToken")
                    .build();

            testRequest = OAuthLoginRequestDto.builder()
                    .code("Success Code")
                    .build();

            testTokenDto = TokenDto.builder()
                    .accessToken("testIssuedAccessToken")
                    .refreshToken("testIssuedRefreshToken")
                    .build();

            testAccount = AccountEntity.builder()
                    .nickname("테스트용 계정1")
                    .oauthServiceId("test_account_1")
                    .thirdPartyType(ThirdPartyEnum.NAVER)
                    .isActive(true)
                    .build();

            testIpAddress = "IP";

            testNetwork = NetworkEntity.builder()
                    .account(testAccount)
                    .name("testName")
                    .subnet("testSubnet")
                    .mtu(1000)
                    .enableIcc(true)
                    .build();
        }

        @Tag("business")
        @Test
        @DisplayName("[Account][Business] 네이버 로그인 후 토큰발급을 정상적으로 성공한다")
        void 네이버_로그인_후_토큰발급을_정상적으로_성공한다() {
            // given - mocking
            BDDMockito.given(naverOAuthUtils.oAuthLogin(any()))
                    .willReturn(testOAuthLoginInfoDto);

            BDDMockito.given(tokenIssuerService.issueToken(any(), any(), any()))
                    .willReturn(testTokenDto);

            BDDMockito.given(accountRepository.save(any()))
                    .willReturn(testAccount);

            BDDMockito.willDoNothing().given(accountHistoryService)
                    .saveLoginHistory(any(), any(), anyBoolean(), any());

            BDDMockito.given(dockerNetworkEntityRepository.save(any()))
                    .willReturn(testNetwork);

            // when
            TokenDto actual = accountService.naverOAuthLogin(testIpAddress, testRequest);

            // then
            Assertions.assertEquals(testTokenDto, actual);
        }

        @Tag("business")
        @Test
        @DisplayName("[Account][Business] 네이버 로그인시 통신과정에서 서버의 원인으로 실패한다")
        void 네이버_로그인시_통신과정에서_서버의_원인으로_실패한다() {
            // given - mocking
            BDDMockito.given(naverOAuthUtils.oAuthLogin(any()))
                    .willThrow(new InternalServerException(ErrorCode.THIRD_PARTY_AUTHORIZATION_SERVER_EXCEPTION));

            BDDMockito.willDoNothing().given(accountHistoryService)
                    .saveLoginHistory(any(), any(), anyBoolean(), any());

            // when && then
            Assertions.assertThrows(
                    InternalServerException.class,
                    () -> accountService.naverOAuthLogin(testIpAddress, testRequest)
            );
        }

        @Tag("business")
        @Test
        @DisplayName("[Account][Business] 네이버 로그인시 통신과정에서 클라이언트의 원인으로 실패한다")
        void 네이버_로그인시_통신과정에서_클라이언트의_원인으로_실패한다() {
            // given - mocking
            BDDMockito.given(naverOAuthUtils.oAuthLogin(any()))
                    .willThrow(new CustomBadRequestException(ErrorCode.THIRD_PARTY_CLIENT_EXCEPTION));

            BDDMockito.willDoNothing().given(accountHistoryService)
                    .saveLoginHistory(any(), any(), anyBoolean(), any());

            // when && then
            Assertions.assertThrows(
                    CustomBadRequestException.class,
                    () -> accountService.naverOAuthLogin(testIpAddress, testRequest)
            );
        }

        @Tag("business")
        @Test
        @DisplayName("[Account][Business] 네이버 로그인시 로그인 실패 저장예외가 발생해도 통신과정 Exception이 발생하도록한다")
        void 네이버_로그인시_로그인_실패_저장예외가_발생해도_통신과정Exception이_발생하도록한다() {
            // given - mocking
            BDDMockito.given(naverOAuthUtils.oAuthLogin(any()))
                    .willThrow(new InternalServerException(ErrorCode.THIRD_PARTY_AUTHORIZATION_SERVER_EXCEPTION));

            BDDMockito.willThrow(new HistoryException(ErrorCode.LOGIN_HISTORY_SAVE_EXCEPTION)).given(accountHistoryService)
                    .saveLoginHistory(any(), any(), anyBoolean(), any());

            // when && then
            Assertions.assertThrows(
                    InternalServerException.class,
                    () -> accountService.naverOAuthLogin(testIpAddress, testRequest)
            );
        }

        @Tag("business")
        @Test
        @DisplayName("[Account][Business] 네이버 로그인 후 토큰발급에서 Exception이 발생한다.")
        void 네이버_로그인_후_토큰발급에서_Exception이_발생한다() {
            // given - mocking
            BDDMockito.given(naverOAuthUtils.oAuthLogin(any()))
                    .willReturn(testOAuthLoginInfoDto);

            BDDMockito.given(tokenIssuerService.issueToken(any(), any(), any()))
                    .willThrow(new CustomTokenException(ErrorCode.JWT_ISSUE_EXCEPTION));

            // when && then
            Assertions.assertThrows(
                    CustomTokenException.class,
                    () -> accountService.naverOAuthLogin(testIpAddress, testRequest)
            );
        }

        @Tag("business")
        @Test
        @DisplayName("[Account][Business] 네이버 로그인 후 히스토리 Exception이 발생해도 클라이언트에 정상로그인 응답을준다")
        void 네이버_로그인_후_히스토리Exception이_발생해도_클라이언트에_정상로그인_응답을준다() {
            // given - mocking
            BDDMockito.given(naverOAuthUtils.oAuthLogin(any()))
                    .willReturn(testOAuthLoginInfoDto);

            BDDMockito.given(tokenIssuerService.issueToken(any(), any(), any()))
                    .willReturn(testTokenDto);

            BDDMockito.given(accountRepository.save(any()))
                    .willReturn(testAccount);

            BDDMockito.given(dockerNetworkEntityRepository.save(any()))
                    .willReturn(testNetwork);

            BDDMockito.willThrow(new HistoryException(ErrorCode.LOGIN_HISTORY_SAVE_EXCEPTION)).given(accountHistoryService)
                    .saveLoginHistory(any(), any(), anyBoolean(), any());

            // when
            TokenDto actual = accountService.naverOAuthLogin(testIpAddress, testRequest);

            // then
            Assertions.assertEquals(testTokenDto, actual);
        }
    }


    @Nested
    @DisplayName("[Account][시나리오 테스트] 네이버 OAuth 로그아웃을 테스트한다.")
    class NaverOAuthLogout {

        private GetRequesterDto testRequesterInfo = null;
        private AccountEntity testAccount = null;
        private GenericSingleResponse<Boolean> testSuccessResponse = null;
        private GenericSingleResponse<Boolean> testFailResponse = null;


        protected NaverOAuthLogout() {
            // given - data
            testRequesterInfo = GetRequesterDto.builder()
                    .id("1")
                    .ipAddress("testIpAddress")
                    .oauthAccessToken("testOAuthAccessToken")
                    .build();

            testAccount = AccountEntity.builder()
                    .nickname("테스트용 계정1")
                    .oauthServiceId("test_account_1")
                    .thirdPartyType(ThirdPartyEnum.NAVER)
                    .isActive(true)
                    .build();

            testSuccessResponse = GenericSingleResponse.<Boolean>builder()
                    .data(true)
                    .build();

            testFailResponse = GenericSingleResponse.<Boolean>builder()
                    .data(false)
                    .build();
        }

        @Tag("business")
        @Test
        @DisplayName("[Account][Business] 네이버 로그아웃을 정상적으로 성공한다")
        void 네이버_로그아웃을_정상적으로_성공한다() {
            // given - mocking
            BDDMockito.given(naverOAuthUtils.oAuthLogout(any()))
                    .willReturn(true);

            BDDMockito.given(accountRepository.findByOauthServiceIdEqualsAndThirdPartyTypeEquals(any(), any()))
                    .willReturn(Optional.of(testAccount));

            BDDMockito.willDoNothing().given(accountHistoryService)
                    .saveLogoutHistory(any(), anyBoolean(), any());

            BDDMockito.given(accountRepository.save(any()))
                    .willReturn(testAccount);

            // when
            GenericSingleResponse<Boolean> actual = accountService.naverOAuthLogout(testRequesterInfo);

            // then
            Assertions.assertEquals(testSuccessResponse.data(), actual.data());
        }

        @Tag("business")
        @Test
        @DisplayName("[Account][Business] 네이버 로그아웃을 실패한다")
        void 네이버_로그아웃을_실패한다() {
            // given - mocking
            BDDMockito.given(naverOAuthUtils.oAuthLogout(any()))
                    .willReturn(false);

            BDDMockito.willDoNothing().given(accountHistoryService)
                    .saveLogoutHistory(any(), anyBoolean(), any());

            // when
            GenericSingleResponse<Boolean> actual = accountService.naverOAuthLogout(testRequesterInfo);

            // then
            Assertions.assertEquals(testFailResponse.data(), actual.data());
        }

        @Tag("business")
        @Test
        @DisplayName("[Account][Business] 네이버 로그아웃시, 서버의 원인으로 실패한다")
        void 네이버_로그아웃시_서버의_원인으로_실패한다() {
            // given - mocking
            BDDMockito.given(naverOAuthUtils.oAuthLogout(any()))
                    .willThrow(new InternalServerException(ErrorCode.NAVER_LOGOUT_EXCEPTION));

            BDDMockito.willDoNothing().given(accountHistoryService)
                    .saveLogoutHistory(any(), anyBoolean(), any());

            // when && then
            Assertions.assertThrows(
                    InternalServerException.class,
                    () -> accountService.naverOAuthLogout(testRequesterInfo)
            );
        }

        @Tag("business")
        @Test
        @DisplayName("[Account][Business] 네이버 로그아웃시, 클라이언트의 원인으로 실패한다")
        void 네이버_로그아웃시_클라이언트의_원인으로_실패한다() {
            // given - mocking
            BDDMockito.given(naverOAuthUtils.oAuthLogout(any()))
                    .willThrow(new CustomBadRequestException(ErrorCode.THIRD_PARTY_CLIENT_EXCEPTION));

            BDDMockito.willDoNothing().given(accountHistoryService)
                    .saveLogoutHistory(any(), anyBoolean(), any());

            // when && then
            Assertions.assertThrows(
                    CustomBadRequestException.class,
                    () -> accountService.naverOAuthLogout(testRequesterInfo)
            );
        }

        @Tag("business")
        @Test
        @DisplayName("[Account][Business] 네이버 로그아웃시 로그아웃 실패 저장예외가 발생해도 비즈니스Exception인, 통신과정 Exception이 발생하도록한다")
        void 네이버_로그아웃시_로그인_실패_저장예외가_발생해도_비즈니스Exception인_통신과정Exception이_발생하도록한다() {
            // given - mocking
            BDDMockito.given(naverOAuthUtils.oAuthLogout(any()))
                    .willThrow(new InternalServerException(ErrorCode.THIRD_PARTY_AUTHORIZATION_SERVER_EXCEPTION));

            BDDMockito.willThrow(new HistoryException(ErrorCode.LOGOUT_HISTORY_SAVE_EXCEPTION)).given(accountHistoryService)
                    .saveLogoutHistory(any(), anyBoolean(), any());

            // when && then
            Assertions.assertThrows(
                    InternalServerException.class,
                    () -> accountService.naverOAuthLogout(testRequesterInfo)
            );
        }

        @Tag("business")
        @Test
        @DisplayName("[Account][Business] 네이버 로그아웃시 Token 안에 같이 온 AccountId가 DB에 없는 경우Exception이 발생하도록한다")
        void 네이버_로그아웃시_Token안에_같이_온_AccountId가_DB에_없는_경우_Exception이_발생하도록한다() {
            // given - mocking
            BDDMockito.given(naverOAuthUtils.oAuthLogout(any()))
                    .willReturn(true);

            BDDMockito.willThrow(new CustomTokenException(ErrorCode.ACCOUNT_NOT_EXIST_OAUTH_ID_EXCEPTION)).given(accountRepository)
                    .findByOauthServiceIdEqualsAndThirdPartyTypeEquals(any(), any());

            // when && then
            Assertions.assertThrows(
                    CustomTokenException.class,
                    () -> accountService.naverOAuthLogout(testRequesterInfo)
            );
        }

        @Tag("business")
        @Test
        @DisplayName("[Account][Business] 네이버 로그아웃 후 히스토리 Exception이 발생해도 클라이언트에 정상로그아웃 응답을준다")
        void 네이버_로그아웃_후_히스토리Exception이_발생해도_클라이언트에_정상로그아웃_응답을준다() {
            // given - mocking
            BDDMockito.given(naverOAuthUtils.oAuthLogout(any()))
                    .willReturn(true);

            BDDMockito.given(accountRepository.findByOauthServiceIdEqualsAndThirdPartyTypeEquals(any(), any()))
                    .willReturn(Optional.of(testAccount));

            BDDMockito.given(accountRepository.save(any()))
                    .willReturn(testAccount);

            BDDMockito.willThrow(new HistoryException(ErrorCode.LOGOUT_HISTORY_SAVE_EXCEPTION)).given(accountHistoryService)
                    .saveLogoutHistory(any(), anyBoolean(), any());

            // when
            GenericSingleResponse<Boolean> actual = accountService.naverOAuthLogout(testRequesterInfo);

            // the
            Assertions.assertEquals(testSuccessResponse.data(), actual.data());
        }
    }

    @Nested
    @DisplayName("[Account][시나리오 테스트] 토큰 재발급을 테스트한다.")
    class ReIssueToken {

        ReIssueTokenRequest testReIssueTokenRequest = null;
        ReIssueTokenResponse testReIssueTokenResponse = null;

        String testSecretKey = null;
        String testReissueAccessToken = null;
        AccountEntity testSuccessAccount = null;
        AccountEntity testFailAccount = null;
        Map<String, Claim> testTokenClaims = null;

        protected ReIssueToken() {
            testSecretKey = "testSecretKey";
            testReissueAccessToken = "testReissueAccessToken";

            testReIssueTokenRequest = ReIssueTokenRequest.builder()
                    .refreshToken("testRefreshToken")
                    .build();

            testTokenClaims = JWT.require(Algorithm.HMAC512(testSecretKey))
                    .build()
                    .verify("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJSZXN0IERvY2tlciAtIEpXVCBUb2tlbiIsInRoaXJkUGFydHlUeXBlIjoiS0FLQU8iLCJvYXV0aFNlcnZpY2VJZCI6InRlc3RfYWNjb3VudF8xIiwibmlja25hbWUiOiLthYzsiqTtirjsmqkg6rOE7KCVMSIsImV4cCI6MTcyNTQxNzIzNX0.GKoZiA-xtphbatP8h_PaAtjdL7711dtORbW1ZiCWET9XCNWJXAjNH0eblc4Pu9J93IfzNBKKYQwljdydXhpMDA")
                    .getClaims();

            testSuccessAccount = AccountEntity.builder()
                    .nickname("테스트용 계정1")
                    .oauthServiceId("test_account_1")
                    .thirdPartyType(ThirdPartyEnum.KAKAO)
                    .isActive(true)
                    .build();
            testSuccessAccount.setMyServiceToken(
                    "testAccessToken",
                    "testRefreshToken"
            );

            testFailAccount = AccountEntity.builder()
                    .nickname("테스트용 계정1")
                    .oauthServiceId("test_account_1")
                    .thirdPartyType(ThirdPartyEnum.KAKAO)
                    .isActive(true)
                    .build();
            testFailAccount.setMyServiceToken(
                    "testDifferentAccessToken",
                    "testDifferentRefreshToken"
            );

            testReIssueTokenResponse = ReIssueTokenResponse.builder()
                    .accessToken(testReissueAccessToken)
                    .build();
        }

        @Tag("business")
        @Test
        @DisplayName("[Account][Business] 토큰 재발급을 정상적으로 성공한다")
        void 토큰_재발급을_정상적으로_성공한다() {
            // given - mocking
            BDDMockito.given(tokenIssuerService.verifyRefreshToken(testReIssueTokenRequest.refreshToken()))
                    .willReturn(testTokenClaims);

            BDDMockito.given(accountRepository.findByOauthServiceIdEqualsAndThirdPartyTypeEquals(any(), any()))
                    .willReturn(Optional.of(testSuccessAccount));

            BDDMockito.given(jwtProperties.getACCESS_TOKEN_EXPIRATION_TIME())
                            .willReturn(1);

            BDDMockito.given(tokenIssuerService.issueToken(any(), any(), any(), anyInt()))
                    .willReturn(testReissueAccessToken);

            BDDMockito.given(accountRepository.save(any()))
                    .willReturn(testSuccessAccount);

            // when
            ReIssueTokenResponse actual = accountService.reIssueToken(testReIssueTokenRequest);

            // then
            Assertions.assertEquals(testReIssueTokenResponse.accessToken(), actual.accessToken());
        }

        @Tag("business")
        @Test
        @DisplayName("[Account][Business] 토큰안의 정보가 DB에 없을경우 예외를 발생시킨다")
        void 토큰안의_정보가_DB에_없을경우_예외를_발생시킨다() {
            // given - mocking
            BDDMockito.given(tokenIssuerService.verifyRefreshToken(testReIssueTokenRequest.refreshToken()))
                    .willReturn(testTokenClaims);

            BDDMockito.given(accountRepository.findByOauthServiceIdEqualsAndThirdPartyTypeEquals(any(), any()))
                    .willReturn(Optional.empty());

            // when && then
            Assertions.assertThrows(
                    CustomTokenException.class,
                    () -> accountService.reIssueToken(testReIssueTokenRequest)
            );
        }

        @Tag("business")
        @Test
        @DisplayName("[Account][Business] 요청으로 RefreshToken이 DB에 저장된 RefreshToken과 다를경우 예외를 발생시킨다")
        void 요청으로_RefreshToken이_DB에_저장된_RefreshToken이_다를경우_예외를_발생시킨다() {
            // given - mocking
            BDDMockito.given(tokenIssuerService.verifyRefreshToken(testReIssueTokenRequest.refreshToken()))
                    .willReturn(testTokenClaims);

            BDDMockito.given(accountRepository.findByOauthServiceIdEqualsAndThirdPartyTypeEquals(any(), any()))
                    .willReturn(Optional.of(testFailAccount));

            // when && then
            Assertions.assertThrows(
                    CustomTokenException.class,
                    () -> accountService.reIssueToken(testReIssueTokenRequest)
            );
        }
    }
}
