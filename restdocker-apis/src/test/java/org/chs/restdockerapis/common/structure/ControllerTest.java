package org.chs.restdockerapis.common.structure;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.chs.domain.common.enumerate.ThirdPartyEnum;
import org.chs.restdockerapis.common.config.RestDocsConfig;
import org.chs.restdockerapis.common.jwt.principal.AccountPrincipalDetails;
import org.chs.restdockerapis.image.presentation.dto.DockerCommandRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;


@Import(RestDocsConfig.class)
@ExtendWith(RestDocumentationExtension.class)
public abstract class ControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected RestDocumentationResultHandler restDocs;

    @Autowired
    protected ObjectMapper objectMapper;

    @BeforeEach
    void setMockMvc(final WebApplicationContext context, final RestDocumentationContextProvider provider) {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(MockMvcRestDocumentation.documentationConfiguration(provider))
                .alwaysDo(restDocs)
                .build();
    }

    public void enableAuthentication() {
        SecurityContextHolder.getContext().setAuthentication(getTestAuthentication());
    }

    public void disableAuthentication() {
        SecurityContextHolder.getContext().setAuthentication(null);
    }

    private Authentication getTestAuthentication() {
        return new UsernamePasswordAuthenticationToken(
                AccountPrincipalDetails.builder()
                        .oAuthAccessToken("testOAuthAccessToken")
                        .oAuthRefreshToken("testOAuthRefreshToken")
                        .oAuthServiceId("testOAuthServiceId")
                        .thirdPartyType(ThirdPartyEnum.KAKAO)
                        .build(), null, null
        );
    }

    protected String writeValueAsStringRequest(List<String> commands) throws JsonProcessingException {
        return objectMapper.writeValueAsString(
                DockerCommandRequestDto.builder()
                        .argCommands(commands)
                        .build()
        );
    }
}