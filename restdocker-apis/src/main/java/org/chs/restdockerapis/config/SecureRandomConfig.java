package org.chs.restdockerapis.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.SecureRandom;

@Configuration
public class SecureRandomConfig {

    @Bean
    public SecureRandom secureRandom() {
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.setSeed(System.currentTimeMillis());
        return secureRandom;
    }
}
