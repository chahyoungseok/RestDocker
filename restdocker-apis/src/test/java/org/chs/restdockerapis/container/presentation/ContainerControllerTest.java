package org.chs.restdockerapis.container.presentation;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.chs.domain.container.dto.ContainerDetailElements;
import org.chs.domain.container.dto.ContainerElements;
import org.chs.domain.container.enumerate.ContainerStatusEnum;
import org.chs.restdockerapis.common.exception.CustomBadRequestException;
import org.chs.restdockerapis.common.exception.ErrorCode;
import org.chs.restdockerapis.common.structure.ControllerTest;
import org.chs.restdockerapis.container.application.ContainerService;
import org.chs.restdockerapis.container.presentation.dto.*;
import org.chs.restdockerapis.image.presentation.dto.DockerCommandRequestDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ContainerController.class)
public class ContainerControllerTest extends ControllerTest {

    @MockBean
    private ContainerService containerService;

    @Nested
    @DisplayName("[Container][성공 테스트] 컨트롤러 조회를 테스트한다.")
    class LsContainerSuccess {

        private ContainerElements containerElements = null;
        private LsContainerResponseDto testResponse = null;
        private LsContainerResponseDto testEmptyResponse = null;

        private LsContainerSuccess() {
            // given
            containerElements = ContainerElements.builder()
                    .createDate(LocalDateTime.now())
                    .updateDate(LocalDateTime.now())
                    .name("testContainerName")
                    .imageName("testImageName")
                    .imageTag("latest")
                    .privateIp("172.17.0.1")
                    .outerPort("18080")
                    .innerPort("8080")
                    .status(ContainerStatusEnum.Created)
                    .build();

            testResponse = LsContainerResponseDto.builder()
                    .containerElementsList(List.of(containerElements))
                    .build();

            testEmptyResponse = LsContainerResponseDto.builder()
                    .containerElementsList(List.of())
                    .build();
        }

        @Tag("controller")
        @Test
        @DisplayName("[Container][Controller] LS Container")
        void 컨테이너_조회를_정상적으로_성공한다() throws Exception {
            // given
            BDDMockito.given(containerService.lsContainer(any(), any()))
                    .willReturn(testResponse);

            // when
            ResultActions resultActions = mockMvc.perform(post("/api/v1/container/ls")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(writeCommonRequest(
                            List.of("")
                    ))
            );

            // then
            resultActions
                    .andExpect(status().isOk())
                    .andDo(
                            restDocs.document(
                                    requestFields(
                                            fieldWithPath("argCommands").type(JsonFieldType.ARRAY).description("도커 명령어 인자값 리스트")
                                    ),
                                    responseFields(
                                            fieldWithPath("containerElementsList").type(JsonFieldType.ARRAY).description("컨테이너 구성요소 리스트"),
                                            fieldWithPath("containerElementsList[].createDate").type(JsonFieldType.STRING).description("컨테이너 생성시간"),
                                            fieldWithPath("containerElementsList[].updateDate").type(JsonFieldType.STRING).description("컨테이너 수정시간"),
                                            fieldWithPath("containerElementsList[].name").type(JsonFieldType.STRING).description("컨테이너 이름"),
                                            fieldWithPath("containerElementsList[].imageName").type(JsonFieldType.STRING).description("컨테이너에 기반이 되는 이미지의 이름"),
                                            fieldWithPath("containerElementsList[].imageTag").type(JsonFieldType.STRING).description("컨테이너에 기반이 되는 이미지의 태그"),
                                            fieldWithPath("containerElementsList[].privateIp").type(JsonFieldType.STRING).description("컨테이너 사설 IP"),
                                            fieldWithPath("containerElementsList[].outerPort").type(JsonFieldType.STRING).description("컨테이너 외부 포트"),
                                            fieldWithPath("containerElementsList[].innerPort").type(JsonFieldType.STRING).description("컨테이너 내부 포트"),
                                            fieldWithPath("containerElementsList[].status").type(JsonFieldType.STRING).description("컨테이너 상태 값")
                                    )
                            )
                    );
        }

        @Tag("controller")
        @Test
        @DisplayName("[Container][Controller] LS Container")
        void 인자값이_존재해도_컨테이너_조회를_정상적으로_성공한다() throws Exception {
            // given
            BDDMockito.given(containerService.lsContainer(any(), any()))
                    .willReturn(testResponse);

            // when
            ResultActions resultActions = mockMvc.perform(post("/api/v1/container/ls")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(writeCommonRequest(
                            List.of("-a", "-l")
                    ))
            );

            // then
            resultActions
                    .andExpect(status().isOk())
                    .andDo(
                            restDocs.document(
                                    requestFields(
                                            fieldWithPath("argCommands").type(JsonFieldType.ARRAY).description("도커 명령어 인자값 리스트")
                                    ),
                                    responseFields(
                                            fieldWithPath("containerElementsList").type(JsonFieldType.ARRAY).description("컨테이너 구성요소 리스트"),
                                            fieldWithPath("containerElementsList[].createDate").type(JsonFieldType.STRING).description("컨테이너 생성시간"),
                                            fieldWithPath("containerElementsList[].updateDate").type(JsonFieldType.STRING).description("컨테이너 수정시간"),
                                            fieldWithPath("containerElementsList[].name").type(JsonFieldType.STRING).description("컨테이너 이름"),
                                            fieldWithPath("containerElementsList[].imageName").type(JsonFieldType.STRING).description("컨테이너에 기반이 되는 이미지의 이름"),
                                            fieldWithPath("containerElementsList[].imageTag").type(JsonFieldType.STRING).description("컨테이너에 기반이 되는 이미지의 태그"),
                                            fieldWithPath("containerElementsList[].privateIp").type(JsonFieldType.STRING).description("컨테이너 사설 IP"),
                                            fieldWithPath("containerElementsList[].outerPort").type(JsonFieldType.STRING).description("컨테이너 외부 포트"),
                                            fieldWithPath("containerElementsList[].innerPort").type(JsonFieldType.STRING).description("컨테이너 내부 포트"),
                                            fieldWithPath("containerElementsList[].status").type(JsonFieldType.STRING).description("컨테이너 상태 값")
                                    )
                            )
                    );
        }

        @Tag("controller")
        @Test
        @DisplayName("[Container][Controller] LS Container")
        void 컨테이너_조회시_데이터가_없어도_정상적으로_성공한다() throws Exception {
            // given
            BDDMockito.given(containerService.lsContainer(any(), any()))
                    .willReturn(testEmptyResponse);

            // when
            ResultActions resultActions = mockMvc.perform(post("/api/v1/container/ls")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(writeCommonRequest(
                            List.of("")
                    ))
            );

            // then
            resultActions
                    .andExpect(status().isOk())
                    .andDo(
                            restDocs.document(
                                    requestFields(
                                            fieldWithPath("argCommands").type(JsonFieldType.ARRAY).description("도커 명령어 인자값 리스트")
                                    ),
                                    responseFields(
                                            fieldWithPath("containerElementsList").type(JsonFieldType.ARRAY).description("컨테이너 구성요소 리스트")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("[Container][실패 테스트] 컨트롤러 조회를 테스트한다.")
    class LsContainerFail {

        private ContainerElements containerElements = null;

        private LsContainerFail() {
            // given
            containerElements = ContainerElements.builder()
                    .createDate(LocalDateTime.now())
                    .updateDate(LocalDateTime.now())
                    .name("testContainerName")
                    .imageName("testImageName")
                    .imageTag("latest")
                    .privateIp("172.17.0.1")
                    .outerPort("18080")
                    .innerPort("8080")
                    .status(ContainerStatusEnum.Created)
                    .build();
        }

        @Tag("controller")
        @Test
        @DisplayName("[Container][Controller] LS Container")
        void 컨테이너_조회시_유효한_인자값이_아니어서_실패한다() throws Exception {
            // given
            BDDMockito.given(containerService.lsContainer(any(), any()))
                    .willThrow(new CustomBadRequestException(ErrorCode.ARGUMENT_COMMAND_NOT_VALID_EXCEPTION));

            // when
            ResultActions resultActions = mockMvc.perform(post("/api/v1/container/ls")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(writeCommonRequest(
                            List.of("-t")
                    ))
            );

            // then
            resultActions
                    .andExpect(status().isBadRequest())
                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof CustomBadRequestException))
                    .andDo(
                            restDocs.document(
                                    requestFields(
                                            fieldWithPath("argCommands").type(JsonFieldType.ARRAY).description("도커 명령어 인자값 리스트")
                                    )
                            )
                    );
        }

        @Tag("controller")
        @Test
        @DisplayName("[Container][Controller] LS Container")
        void 컨테이너_조회시_같은_인자값이_중복되어_실패한다() throws Exception {
            // given
            BDDMockito.given(containerService.lsContainer(any(), any()))
                    .willThrow(new CustomBadRequestException(ErrorCode.ARGUMENT_COMMAND_NOT_VALID_EXCEPTION));

            // when
            ResultActions resultActions = mockMvc.perform(post("/api/v1/container/ls")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(writeCommonRequest(
                            List.of("-a", "-a")
                    ))
            );

            // then
            resultActions
                    .andExpect(status().isBadRequest())
                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof CustomBadRequestException))
                    .andDo(
                            restDocs.document(
                                    requestFields(
                                            fieldWithPath("argCommands").type(JsonFieldType.ARRAY).description("도커 명령어 인자값 리스트")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("[Container][성공 테스트] 컨트롤러 자세한 정보 조회를 테스트한다.")
    class InspectContainerSuccess {

        private ContainerDetailElements containerDetailElements = null;
        private InspectContainerResponseDto testResponse = null;
        private InspectContainerResponseDto testEmptyResponse = null;

        private InspectContainerSuccess() {
            // given
            containerDetailElements = ContainerDetailElements.builder()
                    .createDate(LocalDateTime.now())
                    .updateDate(LocalDateTime.now())
                    .name("testContainerName")
                    .imageName("testImageName")
                    .imageTag("latest")
                    .privateIp("172.17.0.1")
                    .outerPort("18080")
                    .innerPort("8080")
                    .stopRm(true)
                    .status(ContainerStatusEnum.Created)
                    .build();

            testResponse = InspectContainerResponseDto.builder()
                    .inspectContainerDetailElements(containerDetailElements)
                    .build();

            testEmptyResponse = InspectContainerResponseDto.builder()
                    .inspectContainerDetailElements(null)
                    .build();
        }

        @Tag("controller")
        @Test
        @DisplayName("[Container][Controller] Inspect Container")
        void 컨테이너_자세한_조회를_정상적으로_성공한다() throws Exception {
            // given
            BDDMockito.given(containerService.inspectContainer(any(), any()))
                    .willReturn(testResponse);

            // when
            ResultActions resultActions = mockMvc.perform(post("/api/v1/container/inspect")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(writeCommonRequest(
                            List.of("testContainerName")
                    ))
            );

            // then
            resultActions
                    .andExpect(status().isOk())
                    .andDo(
                            restDocs.document(
                                    requestFields(
                                            fieldWithPath("argCommands").type(JsonFieldType.ARRAY).description("도커 명령어 인자값 리스트")
                                    ),
                                    responseFields(
                                            fieldWithPath("inspectContainerDetailElements.createDate").type(JsonFieldType.STRING).description("컨테이너 생성시간"),
                                            fieldWithPath("inspectContainerDetailElements.updateDate").type(JsonFieldType.STRING).description("컨테이너 수정시간"),
                                            fieldWithPath("inspectContainerDetailElements.name").type(JsonFieldType.STRING).description("컨테이너 이름"),
                                            fieldWithPath("inspectContainerDetailElements.imageName").type(JsonFieldType.STRING).description("컨테이너에 기반이 되는 이미지의 이름"),
                                            fieldWithPath("inspectContainerDetailElements.imageTag").type(JsonFieldType.STRING).description("컨테이너에 기반이 되는 이미지의 태그"),
                                            fieldWithPath("inspectContainerDetailElements.privateIp").type(JsonFieldType.STRING).description("컨테이너 사설 IP"),
                                            fieldWithPath("inspectContainerDetailElements.outerPort").type(JsonFieldType.STRING).description("컨테이너 외부 포트"),
                                            fieldWithPath("inspectContainerDetailElements.innerPort").type(JsonFieldType.STRING).description("컨테이너 내부 포트"),
                                            fieldWithPath("inspectContainerDetailElements.status").type(JsonFieldType.STRING).description("컨테이너 상태 값"),
                                            fieldWithPath("inspectContainerDetailElements.stopRm").type(JsonFieldType.BOOLEAN).description("컨테이너 중지 시 삭제여부")
                                    )
                            )
                    );
        }

        @Tag("controller")
        @Test
        @DisplayName("[Container][Controller] Inspect Container")
        void 컨테이너_조회시_데이터가_없어도_정상적으로_성공한다() throws Exception {
            // given
            BDDMockito.given(containerService.inspectContainer(any(), any()))
                    .willReturn(testEmptyResponse);

            // when
            ResultActions resultActions = mockMvc.perform(post("/api/v1/container/inspect")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(writeCommonRequest(
                            List.of("testContainerName")
                    ))
            );

            // then
            resultActions
                    .andExpect(status().isOk())
                    .andDo(
                            restDocs.document(
                                    requestFields(
                                            fieldWithPath("argCommands").type(JsonFieldType.ARRAY).description("도커 명령어 인자값 리스트")
                                    ),
                                    responseFields(
                                            fieldWithPath("inspectContainerDetailElements").type(JsonFieldType.NULL).description("컨테이너의 자세한 정보")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("[Container][실패 테스트] 컨트롤러 자세한 정보 조회를 테스트한다.")
    class InspectContainerFail {

        @Tag("controller")
        @Test
        @DisplayName("[Container][Controller] Inspect Container")
        void 자세한_컨테이너_정보조회시_인자값이_2개_이상이여서_실패한다() throws Exception {
            // given
            BDDMockito.given(containerService.inspectContainer(any(), any()))
                    .willThrow(new CustomBadRequestException(ErrorCode.ARGUMENT_COMMAND_NOT_VALID_EXCEPTION));

            // when
            ResultActions resultActions = mockMvc.perform(post("/api/v1/container/inspect")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(writeCommonRequest(
                            List.of("testContainerName1", "testContainerName2")
                    ))
            );

            // then
            resultActions
                    .andExpect(status().isBadRequest())
                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof CustomBadRequestException))
                    .andDo(
                            restDocs.document(
                                    requestFields(
                                            fieldWithPath("argCommands").type(JsonFieldType.ARRAY).description("도커 명령어 인자값 리스트")
                                    )
                            )
                    );
        }

        @Tag("controller")
        @Test
        @DisplayName("[Container][Controller] Inspect Container")
        void 자세한_컨테이너_정보조회시_인자값이_없어서_실패한다() throws Exception {
            // given
            BDDMockito.given(containerService.inspectContainer(any(), any()))
                    .willThrow(new CustomBadRequestException(ErrorCode.ARGUMENT_COMMAND_NOT_VALID_EXCEPTION));

            // when
            ResultActions resultActions = mockMvc.perform(post("/api/v1/container/inspect")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(writeCommonRequest(
                            List.of()
                    ))
            );

            // then
            resultActions
                    .andExpect(status().isBadRequest())
                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof CustomBadRequestException))
                    .andDo(
                            restDocs.document(
                                    requestFields(
                                            fieldWithPath("argCommands").type(JsonFieldType.ARRAY).description("도커 명령어 인자값 리스트")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("[Container][성공 테스트] 컨트롤러 이름 변경을 테스트한다.")
    class RenameContainerSuccess {

        private RenameContainerResponseDto testSuccessResponse = null;

        private RenameContainerSuccess() {
            // given
            testSuccessResponse = RenameContainerResponseDto.builder()
                    .renameResult(true)
                    .build();
        }

        @Tag("controller")
        @Test
        @DisplayName("[Container][Controller] Rename Container")
        void 컨테이너_이름변경을_정상적으로_성공한다() throws Exception {
            // given
            BDDMockito.given(containerService.renameContainer(any(), any()))
                    .willReturn(testSuccessResponse);

            // when
            ResultActions resultActions = mockMvc.perform(post("/api/v1/container/rename")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(writeCommonRequest(
                            List.of("preContainerName", "postContainerName")
                    ))
            );

            // then
            resultActions
                    .andExpect(status().isOk())
                    .andDo(
                            restDocs.document(
                                    requestFields(
                                            fieldWithPath("argCommands").type(JsonFieldType.ARRAY).description("도커 명령어 인자값 리스트")
                                    ),
                                    responseFields(
                                            fieldWithPath("renameResult").type(JsonFieldType.BOOLEAN).description("컨테이너 이름 변경 성공유무")
                                    )
                            )
                    );
        }

        @Tag("controller")
        @Test
        @DisplayName("[Container][Controller] Rename Container")
        void 컨테이너_이름변경_전과_후가_같아도_정상적으로_성공한다() throws Exception {
            // given
            BDDMockito.given(containerService.renameContainer(any(), any()))
                    .willReturn(testSuccessResponse);

            // when
            ResultActions resultActions = mockMvc.perform(post("/api/v1/container/rename")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(writeCommonRequest(
                            List.of("sameContainerName", "sameContainerName")
                    ))
            );

            // then
            resultActions
                    .andExpect(status().isOk())
                    .andDo(
                            restDocs.document(
                                    requestFields(
                                            fieldWithPath("argCommands").type(JsonFieldType.ARRAY).description("도커 명령어 인자값 리스트")
                                    ),
                                    responseFields(
                                            fieldWithPath("renameResult").type(JsonFieldType.BOOLEAN).description("컨테이너 이름 변경 성공유무")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("[Container][실패 테스트] 컨트롤러 이름 변경을 테스트한다.")
    class RenameContainerFail {

        private RenameContainerResponseDto testFailResponse = null;

        private RenameContainerFail() {
            // given
            testFailResponse = RenameContainerResponseDto.builder()
                    .renameResult(false)
                    .build();
        }

        @Tag("controller")
        @Test
        @DisplayName("[Container][Controller] Rename Container")
        void 자세한_컨테이너_이름변경시_인자값이_2개가_아닌경우_실패한다() throws Exception {
            // given
            BDDMockito.given(containerService.renameContainer(any(), any()))
                    .willThrow(new CustomBadRequestException(ErrorCode.ARGUMENT_COMMAND_NOT_VALID_EXCEPTION));

            // when
            ResultActions resultActions = mockMvc.perform(post("/api/v1/container/rename")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(writeCommonRequest(
                            List.of("containerName")
                    ))
            );

            // then
            resultActions
                    .andExpect(status().isBadRequest())
                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof CustomBadRequestException))
                    .andDo(
                            restDocs.document(
                                    requestFields(
                                            fieldWithPath("argCommands").type(JsonFieldType.ARRAY).description("도커 명령어 인자값 리스트")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("[Container][성공 테스트] 컨트롤러 생성을 테스트한다.")
    class CreateContainerSuccess {

        private CreateContainerResponseDto testResponse = null;

        private CreateContainerSuccess() {
            // given
            testResponse = CreateContainerResponseDto.builder()
                    .containerName("createdContainerName")
                    .build();
        }

        @Tag("controller")
        @Test
        @DisplayName("[Container][Controller] Create Container")
        void 모든_인자값을_가진_컨테이너_생성을_정상적으로_성공한다() throws Exception {
            // given
            BDDMockito.given(containerService.createContainer(any(), any()))
                    .willReturn(testResponse);

            // when
            ResultActions resultActions = mockMvc.perform(post("/api/v1/container/create")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(writeCommonRequest(
                            List.of(
                                    "--name createdContainerName",
                                    "-rm",
                                    "--net bridge",
                                    "--ip 172.17.1.1",
                                    "-p 18080:8080",
                                    "containerName:latest"
                            )
                    ))
            );

            // then
            resultActions
                    .andExpect(status().isOk())
                    .andDo(
                            restDocs.document(
                                    requestFields(
                                            fieldWithPath("argCommands").type(JsonFieldType.ARRAY).description("도커 명령어 인자값 리스트")
                                    ),
                                    responseFields(
                                            fieldWithPath("containerName").type(JsonFieldType.STRING).description("컨테이너 이름 변경 성공유무")
                                    )
                            )
                    );
        }

        @Tag("controller")
        @Test
        @DisplayName("[Container][Controller] Create Container")
        void 내부IP를_명시적_선언하지_않아도_자동할당하여_생성을_정상적으로_성공한다() throws Exception {
            // given
            BDDMockito.given(containerService.createContainer(any(), any()))
                    .willReturn(testResponse);

            // when
            ResultActions resultActions = mockMvc.perform(post("/api/v1/container/create")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(writeCommonRequest(
                            List.of(
                                    "--name createdContainerName",
                                    "-rm",
                                    "--net bridge",
                                    "-p 18080:8080",
                                    "containerName:latest"
                            )
                    ))
            );

            // then
            resultActions
                    .andExpect(status().isOk())
                    .andDo(
                            restDocs.document(
                                    requestFields(
                                            fieldWithPath("argCommands").type(JsonFieldType.ARRAY).description("도커 명령어 인자값 리스트")
                                    ),
                                    responseFields(
                                            fieldWithPath("containerName").type(JsonFieldType.STRING).description("컨테이너 이름 변경 성공유무")
                                    )
                            )
                    );
        }

        @Tag("controller")
        @Test
        @DisplayName("[Container][Controller] Create Container")
        void 네트워크를_명시적_선언하지_않아도_Bridge네트워크로_자동할당하여_생성을_정상적으로_성공한다() throws Exception {
            // given
            BDDMockito.given(containerService.createContainer(any(), any()))
                    .willReturn(testResponse);

            // when
            ResultActions resultActions = mockMvc.perform(post("/api/v1/container/create")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(writeCommonRequest(
                            List.of(
                                    "--name createdContainerName",
                                    "-rm",
                                    "-p 18080:8080",
                                    "containerName:latest"
                            )
                    ))
            );

            // then
            resultActions
                    .andExpect(status().isOk())
                    .andDo(
                            restDocs.document(
                                    requestFields(
                                            fieldWithPath("argCommands").type(JsonFieldType.ARRAY).description("도커 명령어 인자값 리스트")
                                    ),
                                    responseFields(
                                            fieldWithPath("containerName").type(JsonFieldType.STRING).description("컨테이너 이름 변경 성공유무")
                                    )
                            )
                    );
        }

        @Tag("controller")
        @Test
        @DisplayName("[Container][Controller] Create Container")
        void 컨테이너_이름과_이미지만_전달하여도_생성을_정상적으로_성공한다() throws Exception {
            // given
            BDDMockito.given(containerService.createContainer(any(), any()))
                    .willReturn(testResponse);

            // when
            ResultActions resultActions = mockMvc.perform(post("/api/v1/container/create")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(writeCommonRequest(
                            List.of(
                                    "--name createdContainerName",
                                    "containerName:latest"
                            )
                    ))
            );

            // then
            resultActions
                    .andExpect(status().isOk())
                    .andDo(
                            restDocs.document(
                                    requestFields(
                                            fieldWithPath("argCommands").type(JsonFieldType.ARRAY).description("도커 명령어 인자값 리스트")
                                    ),
                                    responseFields(
                                            fieldWithPath("containerName").type(JsonFieldType.STRING).description("컨테이너 이름 변경 성공유무")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("[Container][실패 테스트] 컨트롤러 생성을 테스트한다.")
    class CreateContainerFail {

        @Tag("controller")
        @Test
        @DisplayName("[Container][Controller] Create Container")
        void 브릿지가_명시적으로_선언되지_않을_때_내부IP를_명시적으로_선언하면_실패한다() throws Exception {
            // given
            BDDMockito.given(containerService.createContainer(any(), any()))
                    .willThrow(new CustomBadRequestException(ErrorCode.ARGS_NEED_NETWORK));

            // when
            ResultActions resultActions = mockMvc.perform(post("/api/v1/container/create")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(writeCommonRequest(
                            List.of(
                                    "--name containerName",
                                    "--ip 172.17.1.1",
                                    "containerName:latest"
                            )
                    ))
            );

            // then
            resultActions
                    .andExpect(status().isBadRequest())
                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof CustomBadRequestException))
                    .andDo(
                            restDocs.document(
                                    requestFields(
                                            fieldWithPath("argCommands").type(JsonFieldType.ARRAY).description("도커 명령어 인자값 리스트")
                                    )
                            )
                    );
        }

        @Tag("controller")
        @Test
        @DisplayName("[Container][Controller] Create Container")
        void 같은_도커_호스트안에_내부IP가_겹치면_실패한다() throws Exception {
            // given
            BDDMockito.given(containerService.createContainer(any(), any()))
                    .willThrow(new CustomBadRequestException(ErrorCode.NOT_VALID_PRIVATEIP));

            // when
            ResultActions resultActions = mockMvc.perform(post("/api/v1/container/create")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(writeCommonRequest(
                            List.of(
                                    "--name existPrivateIpContainer",
                                    "-net bridge",
                                    "--ip 172.17.0.1",
                                    "containerName:latest"
                            )
                    ))
            );

            // then
            resultActions
                    .andExpect(status().isBadRequest())
                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof CustomBadRequestException))
                    .andDo(
                            restDocs.document(
                                    requestFields(
                                            fieldWithPath("argCommands").type(JsonFieldType.ARRAY).description("도커 명령어 인자값 리스트")
                                    )
                            )
                    );
        }

        @Tag("controller")
        @Test
        @DisplayName("[Container][Controller] Create Container")
        void 같은_호스트안에_컨테이너_이름이_겹치면_실패한다() throws Exception {
            // given
            BDDMockito.given(containerService.createContainer(any(), any()))
                    .willThrow(new CustomBadRequestException(ErrorCode.NOT_VALID_NAME));

            // when
            ResultActions resultActions = mockMvc.perform(post("/api/v1/container/create")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(writeCommonRequest(
                            List.of(
                                    "--name existContainerNameContainer",
                                    "-net bridge",
                                    "--ip 172.17.0.1",
                                    "containerName:latest"
                            )
                    ))
            );

            // then
            resultActions
                    .andExpect(status().isBadRequest())
                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof CustomBadRequestException))
                    .andDo(
                            restDocs.document(
                                    requestFields(
                                            fieldWithPath("argCommands").type(JsonFieldType.ARRAY).description("도커 명령어 인자값 리스트")
                                    )
                            )
                    );
        }

        @Tag("controller")
        @Test
        @DisplayName("[Container][Controller] Create Container")
        void 같은_호스트안에_외부_Port가_겹치면_실패한다() throws Exception {
            // given
            BDDMockito.given(containerService.createContainer(any(), any()))
                    .willThrow(new CustomBadRequestException(ErrorCode.NOT_VALID_NAME));

            // when
            ResultActions resultActions = mockMvc.perform(post("/api/v1/container/create")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(writeCommonRequest(
                            List.of(
                                    "--name existOuterPortContainer",
                                    "-net bridge",
                                    "--ip 172.17.0.1",
                                    "-p 8080:8080",
                                    "containerName:latest"
                            )
                    ))
            );

            // then
            resultActions
                    .andExpect(status().isBadRequest())
                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof CustomBadRequestException))
                    .andDo(
                            restDocs.document(
                                    requestFields(
                                            fieldWithPath("argCommands").type(JsonFieldType.ARRAY).description("도커 명령어 인자값 리스트")
                                    )
                            )
                    );
        }

        @Tag("controller")
        @Test
        @DisplayName("[Container][Controller] Create Container")
        void 내부IP가_형식에_맞지않으면_실패한다() throws Exception {
            // given
            BDDMockito.given(containerService.createContainer(any(), any()))
                    .willThrow(new CustomBadRequestException(ErrorCode.ARGUMENT_NOT_VALID_EXCEPTION));

            // when
            ResultActions resultActions = mockMvc.perform(post("/api/v1/container/create")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(writeCommonRequest(
                            List.of(
                                    "--name existOuterPortContainer",
                                    "-net bridge",
                                    "--ip 172.17.256.256",
                                    "containerName:latest"
                            )
                    ))
            );

            // then
            resultActions
                    .andExpect(status().isBadRequest())
                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof CustomBadRequestException))
                    .andDo(
                            restDocs.document(
                                    requestFields(
                                            fieldWithPath("argCommands").type(JsonFieldType.ARRAY).description("도커 명령어 인자값 리스트")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("[Container][성공 테스트] 컨트롤러 삭제를 테스트한다.")
    class RmContainerSuccess {

        private RmContainerResponseDto testSuccessResponse = null;

        private RmContainerSuccess() {
            // given
            testSuccessResponse = RmContainerResponseDto.builder()
                    .rmResult(true)
                    .build();
        }

        @Tag("controller")
        @Test
        @DisplayName("[Container][Controller] Rm Container")
        void 컨테이너_삭제를_정상적으로_성공한다() throws Exception {
            // given
            BDDMockito.given(containerService.rmContainer(any(), any()))
                    .willReturn(testSuccessResponse);

            // when
            ResultActions resultActions = mockMvc.perform(post("/api/v1/container/rm")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(writeCommonRequest(
                            List.of("containerName")
                    ))
            );

            // then
            resultActions
                    .andExpect(status().isOk())
                    .andDo(
                            restDocs.document(
                                    requestFields(
                                            fieldWithPath("argCommands").type(JsonFieldType.ARRAY).description("도커 명령어 인자값 리스트")
                                    ),
                                    responseFields(
                                            fieldWithPath("rmResult").type(JsonFieldType.BOOLEAN).description("컨테이너 삭제 성공유무")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("[Container][실패 테스트] 컨트롤러 삭제를 테스트한다.")
    class RmContainerFail {

        @Tag("controller")
        @Test
        @DisplayName("[Container][Controller] Rm Container")
        void 요청한_컨테이너_이름이_없는경우_실패한다() throws Exception {
            // given
            BDDMockito.given(containerService.rmContainer(any(), any()))
                    .willThrow(new CustomBadRequestException(ErrorCode.NOT_EXIST_CONTAINER));

            // when
            ResultActions resultActions = mockMvc.perform(post("/api/v1/container/rm")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(writeCommonRequest(
                            List.of("notExistContainerName")
                    ))
            );

            // then
            resultActions
                    .andExpect(status().isBadRequest())
                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof CustomBadRequestException))
                    .andDo(
                            restDocs.document(
                                    requestFields(
                                            fieldWithPath("argCommands").type(JsonFieldType.ARRAY).description("도커 명령어 인자값 리스트")
                                    )
                            )
                    );
        }

        @Tag("controller")
        @Test
        @DisplayName("[Container][Controller] Rm Container")
        void 요청한_인자값이_하나가_아닌경우_실패한다() throws Exception {
            // given
            BDDMockito.given(containerService.rmContainer(any(), any()))
                    .willThrow(new CustomBadRequestException(ErrorCode.ARGUMENT_COMMAND_NOT_VALID_EXCEPTION));

            // when
            ResultActions resultActions = mockMvc.perform(post("/api/v1/container/rm")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(writeCommonRequest(
                            List.of("-f", "containerName")
                    ))
            );

            // then
            resultActions
                    .andExpect(status().isBadRequest())
                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof CustomBadRequestException))
                    .andDo(
                            restDocs.document(
                                    requestFields(
                                            fieldWithPath("argCommands").type(JsonFieldType.ARRAY).description("도커 명령어 인자값 리스트")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("[Container][성공 테스트] 컨트롤러 실행(생성과 시작)을 테스트한다.")
    class RunContainerSuccess {

        private RunContainerResponseDto testSuccessResponse = null;

        private RunContainerSuccess() {
            // given
            testSuccessResponse = RunContainerResponseDto.builder()
                    .startResult(true)
                    .build();
        }

        @Tag("controller")
        @Test
        @DisplayName("[Container][Controller] Run Container")
        void 컨테이너_실행을_정상적으로_성공한다() throws Exception {
            // given
            BDDMockito.given(containerService.runContainer(any(), any()))
                    .willReturn(testSuccessResponse);

            // when
            ResultActions resultActions = mockMvc.perform(post("/api/v1/container/run")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(writeCommonRequest(
                            List.of(
                                    "--name createdContainerName",
                                    "-rm",
                                    "--net bridge",
                                    "--ip 172.17.1.1",
                                    "-p 18080:8080",
                                    "containerName:latest"
                            )
                    ))
            );

            // then
            resultActions
                    .andExpect(status().isOk())
                    .andDo(
                            restDocs.document(
                                    requestFields(
                                            fieldWithPath("argCommands").type(JsonFieldType.ARRAY).description("도커 명령어 인자값 리스트")
                                    ),
                                    responseFields(
                                            fieldWithPath("startResult").type(JsonFieldType.BOOLEAN).description("컨테이너 삭제 성공유무")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("[Container][실패 테스트] 컨트롤러 실행(생성과 시작)를 테스트한다.")
    class RunContainerFail {

        @Tag("controller")
        @Test
        @DisplayName("[Container][Controller] Run Container")
        void 컨테이너_생성이나_시작_로직에서_실패한경우_실패한다() throws Exception {
            // given
            BDDMockito.given(containerService.runContainer(any(), any()))
                    .willThrow(new CustomBadRequestException(ErrorCode.ARGUMENT_NOT_VALID_EXCEPTION));

            // when
            ResultActions resultActions = mockMvc.perform(post("/api/v1/container/run")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(writeCommonRequest(
                            List.of(
                                    "--name createdContainerName",
                                    "-rm",
                                    "--net bridge",
                                    "--ip 172.17.1.1",
                                    "-p 18080:8080",
                                    "containerName:latest"
                            )
                    ))
            );

            // then
            resultActions
                    .andExpect(status().isBadRequest())
                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof CustomBadRequestException))
                    .andDo(
                            restDocs.document(
                                    requestFields(
                                            fieldWithPath("argCommands").type(JsonFieldType.ARRAY).description("도커 명령어 인자값 리스트")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("[Container][성공 테스트] 컨트롤러 시작을 테스트한다.")
    class StartContainerSuccess {

        private StartContainerResponseDto testSuccessResponse = null;

        private StartContainerSuccess() {
            // given
            testSuccessResponse = StartContainerResponseDto.builder()
                    .startResult(true)
                    .build();
        }

        @Tag("controller")
        @Test
        @DisplayName("[Container][Controller] Start Container")
        void 컨테이너_시작을_정상적으로_성공한다() throws Exception {
            // given
            BDDMockito.given(containerService.startContainer(any(), any()))
                    .willReturn(testSuccessResponse);

            // when
            ResultActions resultActions = mockMvc.perform(post("/api/v1/container/start")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(writeCommonRequest(
                            List.of("containerName")
                    ))
            );

            // then
            resultActions
                    .andExpect(status().isOk())
                    .andDo(
                            restDocs.document(
                                    requestFields(
                                            fieldWithPath("argCommands").type(JsonFieldType.ARRAY).description("도커 명령어 인자값 리스트")
                                    ),
                                    responseFields(
                                            fieldWithPath("startResult").type(JsonFieldType.BOOLEAN).description("컨테이너 삭제 성공유무")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("[Container][실패 테스트] 컨트롤러 시작을 테스트한다.")
    class StartContainerFail {

        @Tag("controller")
        @Test
        @DisplayName("[Container][Controller] Start Container")
        void 요청한_컨테이너가_이미_Running_상태인경우_실패한다() throws Exception {
            // given
            BDDMockito.given(containerService.startContainer(any(), any()))
                    .willThrow(new CustomBadRequestException(ErrorCode.ALREADY_CONTAINER_IS_RUNNING));

            // when
            ResultActions resultActions = mockMvc.perform(post("/api/v1/container/start")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(writeCommonRequest(
                            List.of("containerName")
                    ))
            );

            // then
            resultActions
                    .andExpect(status().isBadRequest())
                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof CustomBadRequestException))
                    .andDo(
                            restDocs.document(
                                    requestFields(
                                            fieldWithPath("argCommands").type(JsonFieldType.ARRAY).description("도커 명령어 인자값 리스트")
                                    )
                            )
                    );
        }

        @Tag("controller")
        @Test
        @DisplayName("[Container][Controller] Start Container")
        void 요청한_인자값이_하나가_아닌경우_실패한다() throws Exception {
            // given
            BDDMockito.given(containerService.startContainer(any(), any()))
                    .willThrow(new CustomBadRequestException(ErrorCode.ARGUMENT_COMMAND_NOT_VALID_EXCEPTION));

            // when
            ResultActions resultActions = mockMvc.perform(post("/api/v1/container/start")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(writeCommonRequest(
                            List.of("-f", "containerName")
                    ))
            );

            // then
            resultActions
                    .andExpect(status().isBadRequest())
                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof CustomBadRequestException))
                    .andDo(
                            restDocs.document(
                                    requestFields(
                                            fieldWithPath("argCommands").type(JsonFieldType.ARRAY).description("도커 명령어 인자값 리스트")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("[Container][성공 테스트] 컨트롤러 중지를 테스트한다.")
    class StopContainerSuccess {

        private StopContainerResponseDto testSuccessResponse = null;

        private StopContainerSuccess() {
            // given
            testSuccessResponse = StopContainerResponseDto.builder()
                    .stopResult(true)
                    .build();
        }

        @Tag("controller")
        @Test
        @DisplayName("[Container][Controller] Stop Container")
        void 컨테이너_중지를_정상적으로_성공한다() throws Exception {
            // given
            BDDMockito.given(containerService.stopContainer(any(), any()))
                    .willReturn(testSuccessResponse);

            // when
            ResultActions resultActions = mockMvc.perform(post("/api/v1/container/stop")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(writeCommonRequest(
                            List.of("containerName")
                    ))
            );

            // then
            resultActions
                    .andExpect(status().isOk())
                    .andDo(
                            restDocs.document(
                                    requestFields(
                                            fieldWithPath("argCommands").type(JsonFieldType.ARRAY).description("도커 명령어 인자값 리스트")
                                    ),
                                    responseFields(
                                            fieldWithPath("stopResult").type(JsonFieldType.BOOLEAN).description("컨테이너 삭제 성공유무")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("[Container][실패 테스트] 컨트롤러 중지를 테스트한다.")
    class StopContainerFail {

        @Tag("controller")
        @Test
        @DisplayName("[Container][Controller] Stop Container")
        void 요청한_컨테이너중_Running_상태인_컨테이너가_없는경우_실패한다() throws Exception {
            // given
            BDDMockito.given(containerService.stopContainer(any(), any()))
                    .willThrow(new CustomBadRequestException(ErrorCode.NOT_EXIST_RUNNING_CONTAINER));

            // when
            ResultActions resultActions = mockMvc.perform(post("/api/v1/container/stop")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(writeCommonRequest(
                            List.of("containerName")
                    ))
            );

            // then
            resultActions
                    .andExpect(status().isBadRequest())
                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof CustomBadRequestException))
                    .andDo(
                            restDocs.document(
                                    requestFields(
                                            fieldWithPath("argCommands").type(JsonFieldType.ARRAY).description("도커 명령어 인자값 리스트")
                                    )
                            )
                    );
        }

        @Tag("controller")
        @Test
        @DisplayName("[Container][Controller] Stop Container")
        void 요청한_인자값이_하나가_아닌경우_실패한다() throws Exception {
            // given
            BDDMockito.given(containerService.stopContainer(any(), any()))
                    .willThrow(new CustomBadRequestException(ErrorCode.ARGUMENT_COMMAND_NOT_VALID_EXCEPTION));

            // when
            ResultActions resultActions = mockMvc.perform(post("/api/v1/container/stop")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(writeCommonRequest(
                            List.of("-f", "containerName")
                    ))
            );

            // then
            resultActions
                    .andExpect(status().isBadRequest())
                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof CustomBadRequestException))
                    .andDo(
                            restDocs.document(
                                    requestFields(
                                            fieldWithPath("argCommands").type(JsonFieldType.ARRAY).description("도커 명령어 인자값 리스트")
                                    )
                            )
                    );
        }
    }


    private String writeCommonRequest(List<String> argCommands) throws JsonProcessingException {
        return objectMapper.writeValueAsString(
                DockerCommandRequestDto.builder()
                        .argCommands(argCommands)
                        .build()
        );
    }
}

