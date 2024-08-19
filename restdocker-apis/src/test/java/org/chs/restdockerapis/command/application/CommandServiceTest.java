package org.chs.restdockerapis.command.application;

import org.chs.restdockerapis.command.presentation.dto.CommandAnalysisRequestDto;
import org.chs.restdockerapis.command.presentation.dto.CommandAnalysisResponseDto;
import org.chs.restdockerapis.common.exception.CustomBadRequestException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class CommandServiceTest {

    @InjectMocks
    private CommandService commandService;

    // 주관적 정의 : 시나리오 테스트란 하나의 메서드를 여러 시나리오에 맞춰 테스트 하는 것을 정의하였다.
    @Nested
    @DisplayName("[Command][시나리오 테스트] 명령어를 분석하고 필터링한다.")
    class FilteringCommand {
        private CommandAnalysisRequestDto testRequest = null;
        private CommandAnalysisResponseDto expected = null;

        @Tag("business")
        @Test
        @DisplayName("[Command][Business] 명령어 분석 및 필터링을 성공한다.")
        void 명령어_분석_및_필터링을_성공한다() {
            // given - data
            testRequest = CommandAnalysisRequestDto.builder()
                    .command("docker run --name containerName --rm -itd --net dockerNetwork --ip restDockerIp -p 8080:10100 -e SPRING_PROFILES_ACTIVE=prod -e TZ=Asia/Seoul restdocker:0.0.1")
                    .build();

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

            expected = CommandAnalysisResponseDto.builder()
                    .url("/api/v1/container/run")
                    .argCommands(argCommands)
                    .build();

            // when
            CommandAnalysisResponseDto actual = commandService.filteringCommand(testRequest);

            // then
            Assertions.assertEquals(expected.getUrl(), actual.getUrl());
            Assertions.assertEquals(expected.getArgCommands(), actual.getArgCommands());
        }

        @Tag("business")
        @Test
        @DisplayName("[Command][Business] 잘못된 MainCommand 를 발견하고, Exception을 내보낸다")
        void 잘못된_MainCommand_를_발견하고_Exception을_내보낸다() {
            // given - data
            testRequest = CommandAnalysisRequestDto.builder()
                    .command("docker reniatnoc ps")
                    .build();

            // when && then
            Assertions.assertThrows(
                    CustomBadRequestException.class,
                    () -> commandService.filteringCommand(testRequest)
            );
        }

        @Tag("business")
        @Test
        @DisplayName("[Command][Business] 잘못된 SubCommand 를 발견하고, Exception을 내보낸다")
        void 잘못된_SubCommand_를_발견하고_Exception을_내보낸다() {
            // given - data
            testRequest = CommandAnalysisRequestDto.builder()
                    .command("docker image ps")
                    .build();

            // when && then
            Assertions.assertThrows(
                    CustomBadRequestException.class,
                    () -> commandService.filteringCommand(testRequest)
            );
        }
    }

}