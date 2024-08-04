package org.chs.restdockerapis.common.config;

import org.chs.restdockerapis.account.presentation.AccountController;
import org.chs.restdockerapis.common.exception.handler.GlobalExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@TestConfiguration
public class AccountMockMvcConfig {

    @Autowired
    private AccountController accountController;

    @Bean
    public MockMvc mockMvc() {
        return MockMvcBuilders
                .standaloneSetup(accountController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }
}
