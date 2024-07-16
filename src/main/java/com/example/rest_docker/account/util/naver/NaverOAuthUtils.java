package com.example.rest_docker.account.util.naver;

import com.example.rest_docker.account.presentation.dto.OAuthTokenDto;
import com.example.rest_docker.account.presentation.dto.naver.NaverOAuthLoginInfoDto;
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

import java.math.BigInteger;
import java.security.SecureRandom;

@Slf4j
@Component
@RequiredArgsConstructor
public class NaverOAuthUtils {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    private final SecureRandom secureRandom;
    private final NaverOAuthInfo naverOAuthInfo;

    public NaverOAuthLoginInfoDto naverOAuthLogin(String authorizationCode) throws RestDockerException {
        OAuthTokenDto accessTokenInfo = getAccessToken(authorizationCode);
        return getAccountInfo(accessTokenInfo);
    }

    public boolean naverOAuthLogout(String oauthAccessToken) throws RestDockerException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        LinkedMultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("client_id", naverOAuthInfo.getCLIENT_ID());
        params.add("client_secret", naverOAuthInfo.getCLIENT_SECRET());
        params.add("access_token", oauthAccessToken);
        params.add("grant_type", naverOAuthInfo.getAUTHORIZATION_GRANT_TYPE_DELETE());

        HttpEntity<LinkedMultiValueMap<String, Object>> request = new HttpEntity<>(params, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                    naverOAuthInfo.getTOKEN_REMOVE_URI(),
                    request,
                    String.class);

            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            return jsonNode.get("result").asText().equals("success");
        } catch(HttpClientErrorException e){
            log.error("Naver Logout 을 완료하지 못하였습니다.");
            throw new RestDockerException(RestDockerExceptionCode.HTTPCLIENT_ERROR_EXCEPTION);
        } catch (JsonProcessingException e) {
            throw new RestDockerException(RestDockerExceptionCode.KAKAO_JSON_PROCESSING_EXCEPTION);
        }
    }

    private OAuthTokenDto getAccessToken(String authorizationCode) throws RestDockerException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", naverOAuthInfo.getAUTHORIZATION_GRANT_TYPE_ISSUE());
        params.add("client_id", naverOAuthInfo.getCLIENT_ID());
        params.add("client_secret", naverOAuthInfo.getCLIENT_SECRET());
        params.add("code", authorizationCode);
        params.add("state", naverStateValue());

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                    naverOAuthInfo.getACCESS_TOKEN_URI(),
                    request,
                    String.class);

            JsonNode jsonNode = objectMapper.readTree(response.getBody());

            return OAuthTokenDto.builder()
                    .accessToken(jsonNode.get("access_token").asText())
                    .refreshToken(jsonNode.get("refresh_token").asText())
                    .build();
        } catch (JsonMappingException e) {
            throw new RestDockerException(RestDockerExceptionCode.KAKAO_JSON_MAPPING_EXCEPTION);
        } catch (JsonProcessingException e) {
            throw new RestDockerException(RestDockerExceptionCode.KAKAO_JSON_PROCESSING_EXCEPTION);
        }
    }

    private NaverOAuthLoginInfoDto getAccountInfo(OAuthTokenDto accessTokenInfo) throws RestDockerException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", "Bearer " + accessTokenInfo.accessToken());

        HttpEntity<LinkedMultiValueMap<String, String>> request = new HttpEntity<>(null, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                    naverOAuthInfo.getACCOUNT_INFO_URI(),
                    request,
                    String.class);

            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            return NaverOAuthLoginInfoDto.builder()
                    .id(jsonNode.get("response").get("id").asText())
                    .nickname(jsonNode.get("response").get("nickname").asText())
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

    public String naverStateValue() {
        return new BigInteger(130, secureRandom).toString(32);
    }
}
