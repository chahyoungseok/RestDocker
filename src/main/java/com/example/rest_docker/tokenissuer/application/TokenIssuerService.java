package com.example.rest_docker.tokenissuer.application;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.example.rest_docker.account.domain.AccountRepository;
import com.example.rest_docker.account.domain.entity.AccountEntity;
import com.example.rest_docker.account.presentation.dto.OAuthLoginResponse;
import com.example.rest_docker.common.enumerate.ThirdPartyEnum;
import com.example.rest_docker.common.exception.RestDockerException;
import com.example.rest_docker.common.exception.RestDockerExceptionCode;
import com.example.rest_docker.common.jwt.JwtProperties;
import com.example.rest_docker.tokenissuer.presentation.dto.ReIssueTokenRequest;
import com.example.rest_docker.tokenissuer.presentation.dto.ReIssueTokenResponse;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class TokenIssuerService {

    private Map<String, Object> header;
    private final JwtProperties jwtProperties;
    private final AccountRepository accountRepository;

    public TokenIssuerService(JwtProperties jwtProperties, AccountRepository accountRepository) {
        header = new HashMap<>();
        header.put(jwtProperties.getTYPE(), jwtProperties.getJWT_TYPE());
        header.put(jwtProperties.getALGORITHM() , jwtProperties.getHMAC512());

        this.jwtProperties = jwtProperties;
        this.accountRepository = accountRepository;
    }

    public OAuthLoginResponse issueToken(String oauthServiceId, String nickname, ThirdPartyEnum thirdPartyType) {

        String accessToken = this.issueToken(oauthServiceId, nickname, thirdPartyType, jwtProperties.getACCESS_TOKEN_EXPIRATION_TIME());
        String refreshToken = this.issueToken(oauthServiceId, nickname, thirdPartyType, jwtProperties.getREFRESH_TOKEN_EXPIRATION_TIME());

        return OAuthLoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    private String issueToken(String oauthServiceId, String nickname, ThirdPartyEnum thirdPartyType, int expiredDate) {
        return JWT.create()
                .withHeader(header)
                .withSubject("Rest Docker - JWT Token")
                .withClaim("thirdPartyType", thirdPartyType.toString())
                .withClaim("oauthServiceId", oauthServiceId)
                .withClaim("nickname", nickname)
                .withExpiresAt(new Date(System.currentTimeMillis() + expiredDate))
                .sign(Algorithm.HMAC512(jwtProperties.getSECRET_KEY()));
    }

    public ReIssueTokenResponse reIssueToken(ReIssueTokenRequest request) throws RestDockerException {
        AccountEntity verifiedAccount = verifyRefreshToken(request.getRefreshToken());

        String reIssueAccessToken = this.issueToken(
                verifiedAccount.getOauthServiceId(),
                verifiedAccount.getNickname(),
                verifiedAccount.getThirdPartyType(),
                jwtProperties.getACCESS_TOKEN_EXPIRATION_TIME()
        );

        verifiedAccount.reIssueAccessToken(reIssueAccessToken);
        accountRepository.save(verifiedAccount);

        return ReIssueTokenResponse.builder()
                .accessToken(reIssueAccessToken)
                .build();
    }

    private AccountEntity verifyRefreshToken(String requestRefreshToken) throws RestDockerException {
        Map<String, Claim> tokenClaims = JWT.require(Algorithm.HMAC512(jwtProperties.getSECRET_KEY()))
                .build()
                .verify(requestRefreshToken)
                .getClaims();

        String oauthServiceId = tokenClaims.get("oauthServiceId").asString();
        String thirdPartyType = tokenClaims.get("thirdPartyType").asString();
        if (null == oauthServiceId || null == thirdPartyType) {
            throw new RestDockerException(RestDockerExceptionCode.ACCOUNT_NOT_EXIST_OAUTH_ID_EXCEPTION);
        }

        Optional<AccountEntity> optionalAccount = accountRepository.findByOauthServiceIdEqualsAndThirdPartyTypeEquals(oauthServiceId, ThirdPartyEnum.valueOf(thirdPartyType));
        if (false == optionalAccount.isPresent()) {
            throw new RestDockerException(RestDockerExceptionCode.ACCOUNT_NOT_EXIST_OAUTH_ID_EXCEPTION);
        }

        AccountEntity verifiedAccount = optionalAccount.get();
        if (false == verifiedAccount.getRefreshToken().equals(requestRefreshToken)) {
            throw new RestDockerException(RestDockerExceptionCode.REFRESH_TOKEN_NOT_MATCH_INFO_EXCEPTION);
        }

        return verifiedAccount;
    }
}
