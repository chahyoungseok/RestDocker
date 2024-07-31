package org.chs.restdockerapis.account.util.kakao;

import lombok.Getter;
import org.chs.restdockerapis.common.yaml.YamlLoadFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Getter
@Configuration
@PropertySource(value = {"application-security.yml"}, factory = YamlLoadFactory.class)
public class KakaoOAuthInfo {

    @Value("${oauth2.kakao.client-id}")
    private String CLIENT_ID;

    @Value("${oauth2.kakao.client-secret}")
    private String CLIENT_SECRET;

    private String ACCESS_TOKEN_URI = "https://kauth.kakao.com/oauth/token";

    private String AUTHORIZATION_GRANT_TYPE = "authorization_code";

    private String REDIRECT_URI = "http://localhost:10100/auth/kakao/callback";

    private String ACCOUNT_INFO_URI = "https://kapi.kakao.com/v2/user/me";

    private String TOKEN_REMOVE_URI = "https://kapi.kakao.com/v1/user/logout";

    private String LOGOUT_TARGET_ID_TYPE = "user_id";
}
