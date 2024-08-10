package org.chs.tokenissuer.application;

import org.chs.globalutils.dto.TokenDto;
import org.chs.tokenissuer.common.properties.JwtProperties;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
class TokenIssuerServiceTest {

    @InjectMocks
    private TokenIssuerService tokenIssuerService;

    @Mock
    private JwtProperties jwtProperties;

    @Nested
    @DisplayName("[Token][시나리오 테스트] 토큰을 발급한다.")
    class IssueToken {

        private String requestOauthServiceId = "testOauthServiceId";
        private String requestNickname = "testNickname";
        private String requestThirdPartyType = "KAKAO";

        private int accessTokenExpirationTime = -1;
        private int refreshTokenExpirationTime = -1;

        private String testSecretKey = null;

        protected IssueToken() {

            accessTokenExpirationTime = 1;
            refreshTokenExpirationTime = 10;
            testSecretKey = "testSecretKey";
        }

        @Tag("business")
        @Test
        @DisplayName("[Token][Business] 정상적으로 토큰을 발급에 성공한다")
        void 정상적으로_토큰을_발급에_성공한다() {
            // given - mocking
            BDDMockito.given(jwtProperties.getSECRET_KEY())
                    .willReturn(testSecretKey);

            BDDMockito.given(jwtProperties.getACCESS_TOKEN_EXPIRATION_TIME())
                    .willReturn(accessTokenExpirationTime);

            BDDMockito.given(jwtProperties.getREFRESH_TOKEN_EXPIRATION_TIME())
                    .willReturn(refreshTokenExpirationTime);

            // when
            TokenDto actual = tokenIssuerService.issueToken(requestOauthServiceId, requestNickname, requestThirdPartyType);

            // then
            Assertions.assertNotNull(actual.accessToken());
            Assertions.assertNotNull(actual.refreshToken());
        }
    }
}