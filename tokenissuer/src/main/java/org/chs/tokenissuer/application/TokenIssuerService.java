package org.chs.tokenissuer.application;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import org.chs.globalutils.dto.TokenDto;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.chs.tokenissuer.common.properties.JwtProperties;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TokenIssuerService {

    private Map<String, Object> header;
    private final JwtProperties jwtProperties;

    @PostConstruct
    public void initialize() {
        header = new HashMap<>();
        header.put(jwtProperties.getTYPE(), jwtProperties.getJWT_TYPE());
        header.put(jwtProperties.getALGORITHM() , jwtProperties.getHMAC512());
    }

    public TokenDto issueToken(String oauthServiceId, String nickname, String thirdPartyType) {

        String accessToken = this.issueToken(oauthServiceId, nickname, thirdPartyType, jwtProperties.getACCESS_TOKEN_EXPIRATION_TIME());
        String refreshToken = this.issueToken(oauthServiceId, nickname, thirdPartyType, jwtProperties.getREFRESH_TOKEN_EXPIRATION_TIME());

        return TokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public String issueToken(String oauthServiceId, String nickname, String thirdPartyType, int expiredDate) {
        return JWT.create()
                .withHeader(header)
                .withSubject("Rest Docker - JWT Token")
                .withClaim("thirdPartyType", thirdPartyType)
                .withClaim("oauthServiceId", oauthServiceId)
                .withClaim("nickname", nickname)
                .withExpiresAt(new Date(System.currentTimeMillis() + expiredDate))
                .sign(Algorithm.HMAC512(jwtProperties.getSECRET_KEY()));
    }

    public Map<String, Claim> verifyRefreshToken(String requestRefreshToken) {
        return JWT.require(Algorithm.HMAC512(jwtProperties.getSECRET_KEY()))
                .build()
                .verify(requestRefreshToken)
                .getClaims();
    }
}
