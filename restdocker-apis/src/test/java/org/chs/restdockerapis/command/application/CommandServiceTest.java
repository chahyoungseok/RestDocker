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
        @DisplayName("[Command][Business] docker images")
        void Docker_Images() {
            // given - data
            testRequest = CommandAnalysisRequestDto.builder()
                    .command("docker images")
                    .build();

            List<String> argCommands = List.of();

            expected = CommandAnalysisResponseDto.builder()
                    .url("/api/v1/image/ls")
                    .argCommands(argCommands)
                    .build();

            // when
            CommandAnalysisResponseDto actual = commandService.filteringCommand(testRequest);

            // then
            Assertions.assertEquals(expected.url(), actual.url());
            Assertions.assertEquals(expected.argCommands(), actual.argCommands());
        }

        @Tag("business")
        @Test
        @DisplayName("[Command][Business] docker image ls")
        void Docker_Image_Ls() {
            // given - data
            testRequest = CommandAnalysisRequestDto.builder()
                    .command("docker image ls")
                    .build();

            List<String> argCommands = List.of();

            expected = CommandAnalysisResponseDto.builder()
                    .url("/api/v1/image/ls")
                    .argCommands(argCommands)
                    .build();

            // when
            CommandAnalysisResponseDto actual = commandService.filteringCommand(testRequest);

            // then
            Assertions.assertEquals(expected.url(), actual.url());
            Assertions.assertEquals(expected.argCommands(), actual.argCommands());
        }

        @Tag("business")
        @Test
        @DisplayName("[Command][Business] docker pull")
        void Docker_Pull() {
            // given - data
            testRequest = CommandAnalysisRequestDto.builder()
                    .command("docker pull")
                    .build();

            List<String> argCommands = List.of();

            expected = CommandAnalysisResponseDto.builder()
                    .url("/api/v1/image/pull")
                    .argCommands(argCommands)
                    .build();

            // when
            CommandAnalysisResponseDto actual = commandService.filteringCommand(testRequest);

            // then
            Assertions.assertEquals(expected.url(), actual.url());
            Assertions.assertEquals(expected.argCommands(), actual.argCommands());
        }

        @Tag("business")
        @Test
        @DisplayName("[Command][Business] docker image pull")
        void Docker_Image_Pull() {
            // given - data
            testRequest = CommandAnalysisRequestDto.builder()
                    .command("docker image pull")
                    .build();

            List<String> argCommands = List.of();

            expected = CommandAnalysisResponseDto.builder()
                    .url("/api/v1/image/pull")
                    .argCommands(argCommands)
                    .build();

            // when
            CommandAnalysisResponseDto actual = commandService.filteringCommand(testRequest);

            // then
            Assertions.assertEquals(expected.url(), actual.url());
            Assertions.assertEquals(expected.argCommands(), actual.argCommands());
        }

        @Tag("business")
        @Test
        @DisplayName("[Command][Business] docker image pull tomcat")
        void Docker_Image_Pull_ArgCommands() {
            // given - data
            testRequest = CommandAnalysisRequestDto.builder()
                    .command("docker pull tomcat")
                    .build();

            List<String> argCommands = List.of("tomcat");

            expected = CommandAnalysisResponseDto.builder()
                    .url("/api/v1/image/pull")
                    .argCommands(argCommands)
                    .build();

            // when
            CommandAnalysisResponseDto actual = commandService.filteringCommand(testRequest);

            // then
            Assertions.assertEquals(expected.url(), actual.url());
            Assertions.assertEquals(expected.argCommands(), actual.argCommands());
        }

        @Tag("business")
        @Test
        @DisplayName("[Command][Business] docker image inspect")
        void Docker_Image_Inspect() {
            // given - data
            testRequest = CommandAnalysisRequestDto.builder()
                    .command("docker image inspect")
                    .build();

            List<String> argCommands = List.of();

            expected = CommandAnalysisResponseDto.builder()
                    .url("/api/v1/image/inspect")
                    .argCommands(argCommands)
                    .build();

            // when
            CommandAnalysisResponseDto actual = commandService.filteringCommand(testRequest);

            // then
            Assertions.assertEquals(expected.url(), actual.url());
            Assertions.assertEquals(expected.argCommands(), actual.argCommands());
        }

        @Tag("business")
        @Test
        @DisplayName("[Command][Business] docker image inspect tomcat")
        void Docker_Image_Inspect_ArgCommands() {
            // given - data
            testRequest = CommandAnalysisRequestDto.builder()
                    .command("docker image inspect tomcat")
                    .build();

            List<String> argCommands = List.of("tomcat");

            expected = CommandAnalysisResponseDto.builder()
                    .url("/api/v1/image/inspect")
                    .argCommands(argCommands)
                    .build();

            // when
            CommandAnalysisResponseDto actual = commandService.filteringCommand(testRequest);

            // then
            Assertions.assertEquals(expected.url(), actual.url());
            Assertions.assertEquals(expected.argCommands(), actual.argCommands());
        }

        @Tag("business")
        @Test
        @DisplayName("[Command][Business] docker image rm")
        void Docker_Image_Rm() {
            // given - data
            testRequest = CommandAnalysisRequestDto.builder()
                    .command("docker image rm")
                    .build();

            List<String> argCommands = List.of();

            expected = CommandAnalysisResponseDto.builder()
                    .url("/api/v1/image/rm")
                    .argCommands(argCommands)
                    .build();

            // when
            CommandAnalysisResponseDto actual = commandService.filteringCommand(testRequest);

            // then
            Assertions.assertEquals(expected.url(), actual.url());
            Assertions.assertEquals(expected.argCommands(), actual.argCommands());
        }

        @Tag("business")
        @Test
        @DisplayName("[Command][Business] docker image rm tomcat")
        void Docker_Image_Rm_ArgCommands() {
            // given - data
            testRequest = CommandAnalysisRequestDto.builder()
                    .command("docker image rm tomcat")
                    .build();

            List<String> argCommands = List.of("tomcat");

            expected = CommandAnalysisResponseDto.builder()
                    .url("/api/v1/image/rm")
                    .argCommands(argCommands)
                    .build();

            // when
            CommandAnalysisResponseDto actual = commandService.filteringCommand(testRequest);

            // then
            Assertions.assertEquals(expected.url(), actual.url());
            Assertions.assertEquals(expected.argCommands(), actual.argCommands());
        }

        @Tag("business")
        @Test
        @DisplayName("[Command][Business] docker network ls")
        void Docker_Network_Ls() {
            // given - data
            testRequest = CommandAnalysisRequestDto.builder()
                    .command("docker network ls")
                    .build();

            List<String> argCommands = List.of();

            expected = CommandAnalysisResponseDto.builder()
                    .url("/api/v1/network/ls")
                    .argCommands(argCommands)
                    .build();

            // when
            CommandAnalysisResponseDto actual = commandService.filteringCommand(testRequest);

            // then
            Assertions.assertEquals(expected.url(), actual.url());
            Assertions.assertEquals(expected.argCommands(), actual.argCommands());
        }

        @Tag("business")
        @Test
        @DisplayName("[Command][Business] docker network ls tomcat")
        void Docker_Network_Ls_ArgCommands() {
            // given - data
            testRequest = CommandAnalysisRequestDto.builder()
                    .command("docker network ls tomcat")
                    .build();

            List<String> argCommands = List.of("tomcat");

            expected = CommandAnalysisResponseDto.builder()
                    .url("/api/v1/network/ls")
                    .argCommands(argCommands)
                    .build();

            // when
            CommandAnalysisResponseDto actual = commandService.filteringCommand(testRequest);

            // then
            Assertions.assertEquals(expected.url(), actual.url());
            Assertions.assertEquals(expected.argCommands(), actual.argCommands());
        }

        @Tag("business")
        @Test
        @DisplayName("[Command][Business] docker network inspect")
        void Docker_Network_Inspect() {
            // given - data
            testRequest = CommandAnalysisRequestDto.builder()
                    .command("docker network inspect")
                    .build();

            List<String> argCommands = List.of();

            expected = CommandAnalysisResponseDto.builder()
                    .url("/api/v1/network/inspect")
                    .argCommands(argCommands)
                    .build();

            // when
            CommandAnalysisResponseDto actual = commandService.filteringCommand(testRequest);

            // then
            Assertions.assertEquals(expected.url(), actual.url());
            Assertions.assertEquals(expected.argCommands(), actual.argCommands());
        }

        @Tag("business")
        @Test
        @DisplayName("[Command][Business] docker network inspect tomcat")
        void Docker_Network_Inspect_ArgCommands() {
            // given - data
            testRequest = CommandAnalysisRequestDto.builder()
                    .command("docker network inspect tomcat")
                    .build();

            List<String> argCommands = List.of("tomcat");

            expected = CommandAnalysisResponseDto.builder()
                    .url("/api/v1/network/inspect")
                    .argCommands(argCommands)
                    .build();

            // when
            CommandAnalysisResponseDto actual = commandService.filteringCommand(testRequest);

            // then
            Assertions.assertEquals(expected.url(), actual.url());
            Assertions.assertEquals(expected.argCommands(), actual.argCommands());
        }

        @Tag("business")
        @Test
        @DisplayName("[Command][Business] docker network create")
        void Docker_Network_Create() {
            // given - data
            testRequest = CommandAnalysisRequestDto.builder()
                    .command("docker network create")
                    .build();

            List<String> argCommands = List.of();

            expected = CommandAnalysisResponseDto.builder()
                    .url("/api/v1/network/create")
                    .argCommands(argCommands)
                    .build();

            // when
            CommandAnalysisResponseDto actual = commandService.filteringCommand(testRequest);

            // then
            Assertions.assertEquals(expected.url(), actual.url());
            Assertions.assertEquals(expected.argCommands(), actual.argCommands());
        }

        @Tag("business")
        @Test
        @DisplayName("[Command][Business] docker network create tomcat -a -p 1234:4321 -z -z")
        void Docker_Network_Create_ArgCommands() {
            // given - data
            testRequest = CommandAnalysisRequestDto.builder()
                    .command("docker network create tomcat -a -p 1234:4321 -z -z")
                    .build();

            List<String> argCommands = List.of("tomcat", "-a", "-p 1234:4321", "-z", "-z");

            expected = CommandAnalysisResponseDto.builder()
                    .url("/api/v1/network/create")
                    .argCommands(argCommands)
                    .build();

            // when
            CommandAnalysisResponseDto actual = commandService.filteringCommand(testRequest);

            // then
            Assertions.assertEquals(expected.url(), actual.url());
            Assertions.assertEquals(expected.argCommands(), actual.argCommands());
        }

        @Tag("business")
        @Test
        @DisplayName("[Command][Business] docker network rm")
        void Docker_Network_Rm() {
            // given - data
            testRequest = CommandAnalysisRequestDto.builder()
                    .command("docker network rm")
                    .build();

            List<String> argCommands = List.of();

            expected = CommandAnalysisResponseDto.builder()
                    .url("/api/v1/network/rm")
                    .argCommands(argCommands)
                    .build();

            // when
            CommandAnalysisResponseDto actual = commandService.filteringCommand(testRequest);

            // then
            Assertions.assertEquals(expected.url(), actual.url());
            Assertions.assertEquals(expected.argCommands(), actual.argCommands());
        }

        @Tag("business")
        @Test
        @DisplayName("[Command][Business] docker network rm tomcat")
        void Docker_Network_Rm_ArgCommands() {
            // given - data
            testRequest = CommandAnalysisRequestDto.builder()
                    .command("docker network rm tomcat")
                    .build();

            List<String> argCommands = List.of("tomcat");

            expected = CommandAnalysisResponseDto.builder()
                    .url("/api/v1/network/rm")
                    .argCommands(argCommands)
                    .build();

            // when
            CommandAnalysisResponseDto actual = commandService.filteringCommand(testRequest);

            // then
            Assertions.assertEquals(expected.url(), actual.url());
            Assertions.assertEquals(expected.argCommands(), actual.argCommands());
        }

        @Tag("business")
        @Test
        @DisplayName("[Command][Business] docker ls")
        void Docker_Ls() {
            // given - data
            testRequest = CommandAnalysisRequestDto.builder()
                    .command("docker ls")
                    .build();

            List<String> argCommands = List.of();

            expected = CommandAnalysisResponseDto.builder()
                    .url("/api/v1/container/ls")
                    .argCommands(argCommands)
                    .build();

            // when
            CommandAnalysisResponseDto actual = commandService.filteringCommand(testRequest);

            // then
            Assertions.assertEquals(expected.url(), actual.url());
            Assertions.assertEquals(expected.argCommands(), actual.argCommands());
        }

        @Tag("business")
        @Test
        @DisplayName("[Command][Business] docker container ls")
        void Docker_Container_Ls() {
            // given - data
            testRequest = CommandAnalysisRequestDto.builder()
                    .command("docker container ls")
                    .build();

            List<String> argCommands = List.of();

            expected = CommandAnalysisResponseDto.builder()
                    .url("/api/v1/container/ls")
                    .argCommands(argCommands)
                    .build();

            // when
            CommandAnalysisResponseDto actual = commandService.filteringCommand(testRequest);

            // then
            Assertions.assertEquals(expected.url(), actual.url());
            Assertions.assertEquals(expected.argCommands(), actual.argCommands());
        }

        @Tag("business")
        @Test
        @DisplayName("[Command][Business] docker container ls tomcat")
        void Docker_Container_Ls_ArgCommands() {
            // given - data
            testRequest = CommandAnalysisRequestDto.builder()
                    .command("docker container ls tomcat")
                    .build();

            List<String> argCommands = List.of("tomcat");

            expected = CommandAnalysisResponseDto.builder()
                    .url("/api/v1/container/ls")
                    .argCommands(argCommands)
                    .build();

            // when
            CommandAnalysisResponseDto actual = commandService.filteringCommand(testRequest);

            // then
            Assertions.assertEquals(expected.url(), actual.url());
            Assertions.assertEquals(expected.argCommands(), actual.argCommands());
        }

        @Tag("business")
        @Test
        @DisplayName("[Command][Business] docker ps")
        void Docker_Ps() {
            // given - data
            testRequest = CommandAnalysisRequestDto.builder()
                    .command("docker ps")
                    .build();

            List<String> argCommands = List.of();

            expected = CommandAnalysisResponseDto.builder()
                    .url("/api/v1/container/ps")
                    .argCommands(argCommands)
                    .build();

            // when
            CommandAnalysisResponseDto actual = commandService.filteringCommand(testRequest);

            // then
            Assertions.assertEquals(expected.url(), actual.url());
            Assertions.assertEquals(expected.argCommands(), actual.argCommands());
        }

        @Tag("business")
        @Test
        @DisplayName("[Command][Business] docker container ps")
        void Docker_Container_Ps() {
            // given - data
            testRequest = CommandAnalysisRequestDto.builder()
                    .command("docker container ps")
                    .build();

            List<String> argCommands = List.of();

            expected = CommandAnalysisResponseDto.builder()
                    .url("/api/v1/container/ps")
                    .argCommands(argCommands)
                    .build();

            // when
            CommandAnalysisResponseDto actual = commandService.filteringCommand(testRequest);

            // then
            Assertions.assertEquals(expected.url(), actual.url());
            Assertions.assertEquals(expected.argCommands(), actual.argCommands());
        }

        @Tag("business")
        @Test
        @DisplayName("[Command][Business] docker container rename")
        void Docker_Container_Rename() {
            // given - data
            testRequest = CommandAnalysisRequestDto.builder()
                    .command("docker container rename")
                    .build();

            List<String> argCommands = List.of();

            expected = CommandAnalysisResponseDto.builder()
                    .url("/api/v1/container/rename")
                    .argCommands(argCommands)
                    .build();

            // when
            CommandAnalysisResponseDto actual = commandService.filteringCommand(testRequest);

            // then
            Assertions.assertEquals(expected.url(), actual.url());
            Assertions.assertEquals(expected.argCommands(), actual.argCommands());
        }

        @Tag("business")
        @Test
        @DisplayName("[Command][Business] docker container rename -f restdocker")
        void Docker_Container_Rename_ArgCommands() {
            // given - data
            testRequest = CommandAnalysisRequestDto.builder()
                    .command("docker container rename -f restdocker")
                    .build();

            List<String> argCommands = List.of("-f restdocker");

            expected = CommandAnalysisResponseDto.builder()
                    .url("/api/v1/container/rename")
                    .argCommands(argCommands)
                    .build();

            // when
            CommandAnalysisResponseDto actual = commandService.filteringCommand(testRequest);

            // then
            Assertions.assertEquals(expected.url(), actual.url());
            Assertions.assertEquals(expected.argCommands(), actual.argCommands());
        }

        @Tag("business")
        @Test
        @DisplayName("[Command][Business] docker container create")
        void Docker_Container_Create() {
            // given - data
            testRequest = CommandAnalysisRequestDto.builder()
                    .command("docker container create")
                    .build();

            List<String> argCommands = List.of();

            expected = CommandAnalysisResponseDto.builder()
                    .url("/api/v1/container/create")
                    .argCommands(argCommands)
                    .build();

            // when
            CommandAnalysisResponseDto actual = commandService.filteringCommand(testRequest);

            // then
            Assertions.assertEquals(expected.url(), actual.url());
            Assertions.assertEquals(expected.argCommands(), actual.argCommands());
        }

        @Tag("business")
        @Test
        @DisplayName("[Command][Business] docker container run")
        void Docker_Container_Run() {
            // given - data
            testRequest = CommandAnalysisRequestDto.builder()
                    .command("docker container run")
                    .build();

            List<String> argCommands = List.of();

            expected = CommandAnalysisResponseDto.builder()
                    .url("/api/v1/container/run")
                    .argCommands(argCommands)
                    .build();

            // when
            CommandAnalysisResponseDto actual = commandService.filteringCommand(testRequest);

            // then
            Assertions.assertEquals(expected.url(), actual.url());
            Assertions.assertEquals(expected.argCommands(), actual.argCommands());
        }

        @Tag("business")
        @Test
        @DisplayName("[Command][Business] docker run --name containerName --rm -itd --net dockerNetwork --ip restDockerIp -p 8080:10100 -e SPRING_PROFILES_ACTIVE=prod -e TZ=Asia/Seoul restdocker:0.0.1")
        void Docker_Run_ArgCommands() {
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
            Assertions.assertEquals(expected.url(), actual.url());
            Assertions.assertEquals(expected.argCommands(), actual.argCommands());
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