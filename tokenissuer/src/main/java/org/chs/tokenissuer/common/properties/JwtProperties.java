package org.chs.tokenissuer.common.properties;

import lombok.Getter;
import org.chs.tokenissuer.common.yaml.YamlLoadFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Getter
@Configuration
@PropertySource(value = {"classpath:application-security.yml"}, factory = YamlLoadFactory.class)
public class JwtProperties {

    @Value("${jwt.hmac512.secret-key}")
    private String SECRET_KEY;

    private final int ACCESS_TOKEN_EXPIRATION_TIME = 1000 * 60 * 60 * 6; // 6시간 (1/1000초)

    private final int REFRESH_TOKEN_EXPIRATION_TIME = 1000 * 60 * 60 * 24 * 10; // 10일 (1/1000초)

    private final String TOKEN_PREFIX = "Bearer ";

    private final String HEADER_STRING = "Authorization";

    private final String TYPE = "typ";

    private final String JWT_TYPE = "JWT";

    private final String ALGORITHM = "alg";

    private final String HMAC512 = "HMAC512";
}
