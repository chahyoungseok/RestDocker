package org.chs.restdockerapis.account.util.kakao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.chs.restdockerapis.account.presentation.dto.common.OAuthTokenDto;
import org.chs.restdockerapis.account.presentation.dto.oauth.OAuthLoginInfoDto;
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

import java.util.Objects;


@Slf4j
@Component
@RequiredArgsConstructor
public class KakaoOAuthUtils {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final KakaoOAuthInfo kakaoOAuthInfo;

    public OAuthLoginInfoDto oAuthLogin(String authorizationCode) {
        OAuthTokenDto accessTokenInfo = getAccessToken(authorizationCode);
        return getAccountInfo(accessTokenInfo);
    }

    public boolean oAuthLogout(String oauthAccessToken, Long accountId) {
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

    private OAuthLoginInfoDto getAccountInfo(OAuthTokenDto accessTokenInfo) {
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
            return OAuthLoginInfoDto.builder()
                    .id(String.valueOf(jsonNode.get("id")))
                    .nickname(String.valueOf(jsonNode.get("properties").get("nickname")))
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
}
