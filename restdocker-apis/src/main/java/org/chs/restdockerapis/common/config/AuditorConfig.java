package org.chs.restdockerapis.common.config;

import org.chs.restdockerapis.common.auditor_aware.RequesterAuditorAware;
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
