package com.example.rest_docker.account.util.kakao;

import com.example.rest_docker.account.presentation.dto.OAuthTokenDto;
import com.example.rest_docker.account.presentation.dto.kakao.KakaoOAuthLoginInfoDto;
import com.example.rest_docker.common.exception.RestDockerException;
import com.example.rest_docker.common.exception.RestDockerExceptionCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;


@Slf4j
@Component
@RequiredArgsConstructor
public class KakaoOAuthUtils {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final KakaoOAuthInfo kakaoOAuthInfo;

    public KakaoOAuthLoginInfoDto kakaoOAuthLogin(String authorizationCode) throws RestDockerException {
        OAuthTokenDto accessTokenInfo = getAccessToken(authorizationCode);
        return getAccountInfo(accessTokenInfo);
    }

    public boolean kakaoOAuthLogout(String oauthAccessToken, Long accountId) throws RestDockerException {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + oauthAccessToken);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        LinkedMultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("target_id_type", kakaoOAuthInfo.getLOGOUT_TARGET_ID_TYPE());
        params.add("target_id", accountId);

        HttpEntity<LinkedMultiValueMap<String, Object>> request = new HttpEntity<>(params, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                    kakaoOAuthInfo.getTOKEN_REMOVE_URI(),
                    request,
                    String.class);

            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            return !Objects.isNull(jsonNode.get("id"));

        } catch(HttpClientErrorException e){
            log.error("Kakao Logout 을 완료하지 못하였습니다.");
            throw new RestDockerException(RestDockerExceptionCode.HTTPCLIENT_ERROR_EXCEPTION);
        } catch (JsonProcessingException e) {
            throw new RestDockerException(RestDockerExceptionCode.KAKAO_JSON_PROCESSING_EXCEPTION);
        }
    }

    private OAuthTokenDto getAccessToken(String authorizationCode) throws RestDockerException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", kakaoOAuthInfo.getAUTHORIZATION_GRANT_TYPE());
        params.add("client_id", kakaoOAuthInfo.getCLIENT_ID());
        params.add("redirect_uri", kakaoOAuthInfo.getREDIRECT_URI());
        params.add("client_secret", kakaoOAuthInfo.getCLIENT_SECRET());
        params.add("code", authorizationCode);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                    kakaoOAuthInfo.getACCESS_TOKEN_URI(),
                    request,
                    String.class);

            JsonNode jsonNode = objectMapper.readTree(response.getBody());

            return OAuthTokenDto.builder()
                    .accessToken(String.valueOf(jsonNode.get("access_token")))
                    .refreshToken(String.valueOf(jsonNode.get("refresh_token")))
                    .build();
        } catch (JsonMappingException e) {
            throw new RestDockerException(RestDockerExceptionCode.KAKAO_JSON_MAPPING_EXCEPTION);
        } catch (JsonProcessingException e) {
            throw new RestDockerException(RestDockerExceptionCode.KAKAO_JSON_PROCESSING_EXCEPTION);
        }
    }

    private KakaoOAuthLoginInfoDto getAccountInfo(OAuthTokenDto accessTokenInfo) throws RestDockerException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", "Bearer " + accessTokenInfo.accessToken());

        HttpEntity<LinkedMultiValueMap<String, String>> request = new HttpEntity<>(null, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                    kakaoOAuthInfo.getACCOUNT_INFO_URI(),
                    request,
                    String.class);

            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            return KakaoOAuthLoginInfoDto.builder()
                    .id(String.valueOf(jsonNode.get("id")))
                    .nickname(String.valueOf(jsonNode.get("properties").get("nickname")))
                    .accessToken(accessTokenInfo.accessToken())
                    .refreshToken(accessTokenInfo.refreshToken())
                    .build();

        } catch(HttpClientErrorException e){
            log.error("Kakao Account Info 를 가져오지 못하였습니다.");
            throw new RestDockerException(RestDockerExceptionCode.HTTPCLIENT_ERROR_EXCEPTION);
        } catch (JsonProcessingException e) {
            throw new RestDockerException(RestDockerExceptionCode.KAKAO_JSON_PROCESSING_EXCEPTION);
        }
    }
}
