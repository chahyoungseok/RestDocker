package org.chs.restdockerapis.account.presentation;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.chs.globalutils.dto.TokenDto;
import org.chs.restdockerapis.account.application.AccountService;
import org.chs.restdockerapis.account.presentation.dto.ReIssueTokenRequest;
import org.chs.restdockerapis.account.presentation.dto.ReIssueTokenResponse;
import org.chs.restdockerapis.account.presentation.dto.common.GenericSingleResponse;
import org.chs.restdockerapis.account.presentation.dto.common.OAuthLoginRequestDto;
import org.chs.restdockerapis.common.config.AccountMockMvcConfig;
import org.chs.restdockerapis.common.structure.ControllerTest;
import org.junit.jupiter.api.*;
import org.mockito.BDDMockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import(AccountMockMvcConfig.class)
public class AccountControllerTest extends ControllerTest {

    @MockBean
    private AccountService accountService;

    @Nested
    @DisplayName("[Account][성공 테스트] OAuth Login을 테스트한다.")
    class OAuthLoginSuccess {

        private String testRequest = null;
        private TokenDto testResponse = null;

        @BeforeEach
        void setUpData() throws JsonProcessingException {
            // given
            testRequest = objectMapper.writeValueAsString(
                    OAuthLoginRequestDto.builder()
                            .code("test_oauth_code")
                            .build()
            );

            testResponse = TokenDto.builder()
                    .accessToken("success_accessToken")
                    .refreshToken("success_refreshToken")
                    .build();
        }

        @Tag("controller")
        @Test
        @DisplayName("[Account] Kakao OAuth Login")
        void 카카오_요청에_대해_특정_접근권한_없이_인증코드만으로_계정이_생성될_수_있는지_테스트한다() throws Exception {
            // given
            BDDMockito.given(accountService.kakaoOAuthLogin(any(), any()))
                    .willReturn(testResponse);

            // when
            ResultActions resultActions = mockMvc.perform(post("/api/v1/account/kakao/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(testRequest)
            );

            // then
            resultActions
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.accessToken").value("success_accessToken"))
                    .andExpect(jsonPath("$.refreshToken").value("success_refreshToken"))
                    .andDo(print());
        }

        @Tag("controller")
        @Test
        @DisplayName("[Account] Naver OAuth Login")
        void 네이버_요청에_대해_특정_접근권한_없이_인증코드만으로_계정이_생성될_수_있는지_테스트한다() throws Exception {
            // given
            BDDMockito.given(accountService.naverOAuthLogin(any(), any()))
                    .willReturn(testResponse);

            // when
            ResultActions resultActions = mockMvc.perform(post("/api/v1/account/naver/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(testRequest)
            );

            // then
            resultActions
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.accessToken").value("success_accessToken"))
                    .andExpect(jsonPath("$.refreshToken").value("success_refreshToken"))
                    .andDo(print());
        }
    }

    @Nested
    @DisplayName("[Account][실패 테스트] OAuth Login을 테스트한다.")
    class OAuthLoginFail {

        @Tag("controller")
        @Test
        @DisplayName("[Account] Kakao OAuth Login")
        void 카카오_요청에_NotEmpty_값인_code_를_넣지_않고_보낸다() throws Exception {
            // given
            String testEmptyRequest = objectMapper.writeValueAsString(OAuthLoginRequestDto.builder().build());

            // when
            ResultActions resultActions = mockMvc.perform(post("/api/v1/account/kakao/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(testEmptyRequest)
            );

            // then
            resultActions
                    .andExpect(status().isBadRequest())
                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
                    .andDo(print());
        }

        @Tag("controller")
        @Test
        @DisplayName("[Account] Naver OAuth Login")
        void 네이버_요청에_NotEmpty_값인_code_를_넣지_않고_보낸다() throws Exception {
            // given
            String testEmptyRequest = objectMapper.writeValueAsString(OAuthLoginRequestDto.builder().build());

            // when
            ResultActions resultActions = mockMvc.perform(post("/api/v1/account/naver/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(testEmptyRequest)
            );

            // then
            resultActions
                    .andExpect(status().isBadRequest())
                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
                    .andDo(print());
        }
    }

    @Nested
    @DisplayName("[Account][성공 테스트] OAuth Logout을 테스트한다.")
    class OAuthLogoutSuccess {

        @Tag("controller")
        @Test
        @DisplayName("[Account] Kakao OAuth Logout")
        void 카카오_요청에_대해_로그아웃을_테스트한다() throws Exception {
            // given
            BDDMockito.given(accountService.kakaoOAuthLogout(any()))
                    .willReturn(
                            GenericSingleResponse.<Boolean>builder()
                                    .data(true)
                                    .build()
                    );

            // when
            ResultActions resultActions = mockMvc.perform(post("/api/v1/account/kakao/logout"));

            // then
            resultActions
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").value(true))
                    .andDo(print());

        }

        @Tag("controller")
        @Test
        @DisplayName("[Account] Naver OAuth Logout")
        void 네이버_요청에_대해_로그아웃을_테스트한다() throws Exception {
            // given
            BDDMockito.given(accountService.naverOAuthLogout(any()))
                    .willReturn(
                            GenericSingleResponse.<Boolean>builder()
                                    .data(true)
                                    .build()
                    );

            // when
            ResultActions resultActions = mockMvc.perform(post("/api/v1/account/naver/logout"));

            // then
            resultActions
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").value(true))
                    .andDo(print());

        }
    }


    @Nested
    @DisplayName("[Account][성공 테스트] Token 재발급을 테스트한다.")
    class ReIssueToken {

        private String testRequest = null;
        private ReIssueTokenResponse testResponse = null;

        @BeforeEach
        void setTestToken() throws JsonProcessingException {
            testRequest = objectMapper.writeValueAsString(
                    ReIssueTokenRequest.builder()
                            .refreshToken("testRefreshToken")
                            .build()
            );

            testResponse = ReIssueTokenResponse.builder()
                    .accessToken("testAccessToken")
                    .build();
        }

        @Tag("controller")
        @Test
        @DisplayName("[Account][성공 테스트] ReIssue AccessToken")
        void RefreshToken에_대해_유효성검증을_거친_후_AccessToken을_발급해준다() throws Exception {
            // given
            BDDMockito.given(accountService.reIssueToken(any()))
                    .willReturn(testResponse);

            // when
            ResultActions resultActions = mockMvc.perform(post("/api/v1/account/reissue")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(testRequest)
            );

            // then
            resultActions
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.accessToken").value("testAccessToken"))
                    .andDo(print());
        }
    }
}