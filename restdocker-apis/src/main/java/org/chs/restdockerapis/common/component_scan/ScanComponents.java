package org.chs.restdockerapis.common.component_scan;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan("org.chs.domain")
@EnableJpaRepositories("org.chs.domain")
public class ScanComponents {
}
