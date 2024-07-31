package org.chs.restdockerapis.account.util.naver;

import lombok.Getter;
import org.chs.restdockerapis.common.yaml.YamlLoadFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Getter
@Configuration
@PropertySource(value = {"application-security.yml"}, factory = YamlLoadFactory.class)
public class NaverOAuthInfo {

    @Value("${oauth2.naver.client-id}")
    private String CLIENT_ID;

    @Value("${oauth2.naver.client-secret}")
    private String CLIENT_SECRET;

    private String REDIRECT_URI = "http://localhost:10100/login/oauth2/code/naver";

    private String ACCESS_TOKEN_URI = "https://nid.naver.com/oauth2.0/token";

    private String AUTHORIZATION_GRANT_TYPE_ISSUE = "authorization_code";

    private String AUTHORIZATION_GRANT_TYPE_REISSUE = "refresh_token";

    private String AUTHORIZATION_GRANT_TYPE_DELETE = "delete";

    private String ACCOUNT_INFO_URI = "https://openapi.naver.com/v1/nid/me";

    private String TOKEN_REMOVE_URI = "https://nid.naver.com/oauth2.0/token";
}
