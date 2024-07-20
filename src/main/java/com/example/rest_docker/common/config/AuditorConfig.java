package com.example.rest_docker.common.config;

import com.example.rest_docker.common.auditor_aware.RequesterAuditorAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing
public class AuditorConfig {

    @Bean
    public AuditorAware<String> auditorAware() {
        return new RequesterAuditorAware();
    }

}
