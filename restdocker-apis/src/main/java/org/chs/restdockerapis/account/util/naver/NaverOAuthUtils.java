package org.chs.restdockerapis.account.util.naver;

import org.chs.restdockerapis.account.presentation.dto.common.OAuthTokenDto;
import org.chs.restdockerapis.account.presentation.dto.naver.NaverOAuthLoginInfoDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.chs.restdockerapis.common.exception.CustomBadRequestException;
import org.chs.restdockerapis.common.exception.ErrorCode;
import org.chs.restdockerapis.common.exception.InternalServerException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
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

    public NaverOAuthLoginInfoDto naverOAuthLogin(String authorizationCode) {
        OAuthTokenDto accessTokenInfo = getAccessToken(authorizationCode);
        return getAccountInfo(accessTokenInfo);
    }

    public boolean naverOAuthLogout(String oauthAccessToken) {
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
        } catch (NullPointerException e){
            throw new CustomBadRequestException(ErrorCode.NULL_POINT_EXCEPTION);
        } catch (HttpClientErrorException e) {
            throw new CustomBadRequestException(ErrorCode.THIRD_PARTY_CLIENT_EXCEPTION);
        } catch (JsonMappingException e) {
            throw new InternalServerException(ErrorCode.JSON_MAPPING_EXCEPTION);
        } catch (JsonProcessingException e) {
            throw new InternalServerException(ErrorCode.JSON_PROCESSING_EXCEPTION);
        } catch (HttpServerErrorException e) {
            throw new InternalServerException(ErrorCode.THIRD_PARTY_AUTHORIZATION_SERVER_EXCEPTION);
        }
    }

    private OAuthTokenDto getAccessToken(String authorizationCode) {
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
        } catch (NullPointerException e){
            throw new CustomBadRequestException(ErrorCode.NULL_POINT_EXCEPTION);
        } catch (HttpClientErrorException e) {
            throw new CustomBadRequestException(ErrorCode.THIRD_PARTY_CLIENT_EXCEPTION);
        } catch (JsonMappingException e) {
            throw new InternalServerException(ErrorCode.JSON_MAPPING_EXCEPTION);
        } catch (JsonProcessingException e) {
            throw new InternalServerException(ErrorCode.JSON_PROCESSING_EXCEPTION);
        } catch (HttpServerErrorException e) {
            throw new InternalServerException(ErrorCode.THIRD_PARTY_AUTHORIZATION_SERVER_EXCEPTION);
        }
    }

    private NaverOAuthLoginInfoDto getAccountInfo(OAuthTokenDto accessTokenInfo) {
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

        } catch (NullPointerException e){
            throw new CustomBadRequestException(ErrorCode.NULL_POINT_EXCEPTION);
        } catch (HttpClientErrorException e) {
            throw new CustomBadRequestException(ErrorCode.THIRD_PARTY_CLIENT_EXCEPTION);
        } catch (JsonMappingException e) {
            throw new InternalServerException(ErrorCode.JSON_MAPPING_EXCEPTION);
        } catch (JsonProcessingException e) {
            throw new InternalServerException(ErrorCode.JSON_PROCESSING_EXCEPTION);
        } catch (HttpServerErrorException e) {
            throw new InternalServerException(ErrorCode.THIRD_PARTY_AUTHORIZATION_SERVER_EXCEPTION);
        }
    }

    public String naverStateValue() {
        return new BigInteger(130, secureRandom).toString(32);
    }
}
