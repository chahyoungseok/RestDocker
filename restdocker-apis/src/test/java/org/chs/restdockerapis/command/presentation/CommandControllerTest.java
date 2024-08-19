package org.chs.restdockerapis.command.presentation;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.chs.restdockerapis.command.application.CommandService;
import org.chs.restdockerapis.command.presentation.dto.CommandAnalysisRequestDto;
import org.chs.restdockerapis.command.presentation.dto.CommandAnalysisResponseDto;
import org.chs.restdockerapis.common.exception.CustomBadRequestException;
import org.chs.restdockerapis.common.exception.ErrorCode;
import org.chs.restdockerapis.common.structure.ControllerTest;
import org.junit.jupiter.api.*;
import org.mockito.BDDMockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommandController.class)
public class CommandControllerTest extends ControllerTest {

    @MockBean
    private CommandService commandService;

    @Nested
    @DisplayName("[Command][성공 테스트] Command 분석 후 필터링 된 결과를 반환한다.")
    class FilteringCommandSuccess {

        private String testRequest = null;
        private CommandAnalysisResponseDto testResponse = null;

        @BeforeEach
        void setUpData() throws JsonProcessingException {
            // given
            testRequest = objectMapper.writeValueAsString(
                    CommandAnalysisRequestDto.builder()
                            .command("docker run --name containerName --rm -itd --net dockerNetwork --ip restDockerIp -p 8080:10100 -e SPRING_PROFILES_ACTIVE=prod -e TZ=Asia/Seoul restdocker:0.0.1")
                            .build()
            );

            List<String> argCommands = Arrays.asList(
                    "--name containerName",
                    "--rm",
                    "-itd",
                    "--net dockerNetwork",
                    "--ip restDockerIp",
                    "-p 8080:10100",
                    "-e SPRING_PROFILES_ACTIVE=prod",
                    "-e TZ=Asia/Seoul",
                    "restdocker:0.0.1"
            );

            testResponse = CommandAnalysisResponseDto.builder()
                    .url("/api/v1/container/run")
                    .argCommands(argCommands)
                    .build();
        }

        @Tag("controller")
        @Test
        @DisplayName("[Command][Controller] Filtering Command")
        void 명령어_분석_후_알맞는_비즈니스_로직을_성공시킨다() throws Exception {
            // given
            BDDMockito.given(commandService.filteringCommand(any()))
                    .willReturn(testResponse);

            // when
            ResultActions resultActions = mockMvc.perform(post("/api/v1/command/filter")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(testRequest)
            );

            // then
            resultActions
                    .andExpect(status().isOk())
                    .andDo(
                            restDocs.document(
                                    requestFields(
                                            fieldWithPath("command").type(JsonFieldType.STRING).description("도커 명령어")
                                    ),
                                    responseFields(
                                            fieldWithPath("url").type(JsonFieldType.STRING).description("명령어에 맞는 URL"),
                                            fieldWithPath("argCommands").type(JsonFieldType.ARRAY).description("필터링 된 인자들")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("[Command][실패 테스트] Command 분석 후 알맞지 않은 명령어 조합이라면 실패한다")
    class FilteringCommandFail {

        private String testRequest = null;

        @BeforeEach
        void setUpData() throws JsonProcessingException {
            // given
            testRequest = objectMapper.writeValueAsString(
                    CommandAnalysisRequestDto.builder()
                            .command("docker run images")
                            .build()
            );
        }

        @Tag("controller")
        @Test
        @DisplayName("[Command][Controller] Filtering Command")
        void 명령어_분석_후_알맞지_않은_명령어_조합이라면_실패한다() throws Exception {
            // given
            BDDMockito.given(commandService.filteringCommand(any()))
                    .willThrow(new CustomBadRequestException(ErrorCode.NOT_CORRECT_SUBCOMMAND));

            // when
            ResultActions resultActions = mockMvc.perform(post("/api/v1/command/filter")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(testRequest)
            );

            // then
            resultActions
                    .andExpect(status().isBadRequest())
                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof CustomBadRequestException))
                    .andDo(
                            restDocs.document(
                                    requestFields(
                                            fieldWithPath("command").type(JsonFieldType.STRING).description("도커 명령어")
                                    )
                            )
                    );
        }
    }
}