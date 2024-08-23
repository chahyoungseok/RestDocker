package org.chs.restdockerapis.image.presentation;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.chs.domain.image.dto.ImageDetailElements;
import org.chs.domain.image.dto.ImageElements;
import org.chs.restdockerapis.common.exception.CustomBadRequestException;
import org.chs.restdockerapis.common.exception.ErrorCode;
import org.chs.restdockerapis.common.structure.ControllerTest;
import org.chs.restdockerapis.image.application.ImageService;
import org.chs.restdockerapis.image.presentation.dto.*;
import org.junit.jupiter.api.*;
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
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ImageController.class)
public class ImageControllerTest extends ControllerTest {

    @MockBean
    private ImageService imageService;

    private String writeValueAsStringRequest(List<String> commands) throws JsonProcessingException {
        return objectMapper.writeValueAsString(
                DockerCommandRequestDto.builder()
                        .argCommands(commands)
                        .build()
        );
    }

    @Nested
    @DisplayName("[Image][성공 테스트] 사용자가 가지고있는 이미지를 응답해준다.")
    class LsImageSuccess {

        private List<ImageElements> responseImageElementList = null;
        private LsImageResponseDto testResponse = null;

        @BeforeEach
        void setUpData() {
            // given
            responseImageElementList = List.of(
                    ImageElements.builder()
                            .createDate(LocalDateTime.parse("2024-01-01T00:00:00"))
                            .updateDate(LocalDateTime.parse("2024-01-01T00:00:00"))
                            .name("tomcat")
                            .tag("latest")
                            .size("490702439")
                            .build()
            );

            testResponse = LsImageResponseDto.builder()
                    .lsImageList(responseImageElementList)
                    .build();
        }


        @Tag("controller")
        @Test
        @DisplayName("[Image][Controller] Ls Image")
        void 사용자가_Pull_받은_Image_정보를_반환한다_인자가_없는경우() throws Exception {
            // given
            BDDMockito.given(imageService.lsImage(any(), any()))
                    .willReturn(testResponse);

            // when
            ResultActions resultActions = mockMvc.perform(post("/api/v1/image/ls")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(writeValueAsStringRequest(List.of()))
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
                                            fieldWithPath("lsImageList").type(JsonFieldType.ARRAY).description("반환된 이미지의 정보"),
                                            fieldWithPath("lsImageList[].createDate").type(JsonFieldType.STRING).description("이미지의 생성날짜"),
                                            fieldWithPath("lsImageList[].updateDate").type(JsonFieldType.STRING).description("이미지의 마지막 수정날짜"),
                                            fieldWithPath("lsImageList[].name").type(JsonFieldType.STRING).description("이미지의 이름"),
                                            fieldWithPath("lsImageList[].tag").type(JsonFieldType.STRING).description("이미지의 태그"),
                                            fieldWithPath("lsImageList[].size").type(JsonFieldType.STRING).description("이미지의 크기")
                                    )
                            )
                    );
        }

        @Tag("controller")
        @Test
        @DisplayName("[Image][Controller] Ls Image")
        void 사용자가_가지고있는_Image_정보를_반환한다_인자에_맞는_데이터가_없는경우() throws Exception {
            // given
            BDDMockito.given(imageService.lsImage(any(), any()))
                    .willReturn(LsImageResponseDto.builder().build());

            // when
            ResultActions resultActions = mockMvc.perform(post("/api/v1/image/ls")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(writeValueAsStringRequest(List.of("cattom")))
            );

            // then
            resultActions
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.lsImageList").doesNotExist())
                    .andDo(
                            restDocs.document(
                                    requestFields(
                                            fieldWithPath("argCommands").type(JsonFieldType.ARRAY).description("도커 명령어 인자값 리스트")
                                    ),
                                    responseFields(
                                            fieldWithPath("lsImageList").type(JsonFieldType.NULL).description("반환된 이미지의 정보")
                                    )
                            )
                    );
        }

        @Tag("controller")
        @Test
        @DisplayName("[Image][Controller] Ls Image")
        void 사용자가_Pull_받은_Image_정보를_반환한다_인자가_있는경우() throws Exception {
            // given
            BDDMockito.given(imageService.lsImage(any(), any()))
                    .willReturn(testResponse);

            // when
            ResultActions resultActions = mockMvc.perform(post("/api/v1/image/ls")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(writeValueAsStringRequest(List.of("tomcat")))
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
                                            fieldWithPath("lsImageList").type(JsonFieldType.ARRAY).description("반환된 이미지의 정보"),
                                            fieldWithPath("lsImageList[].createDate").type(JsonFieldType.STRING).description("이미지의 생성날짜"),
                                            fieldWithPath("lsImageList[].updateDate").type(JsonFieldType.STRING).description("이미지의 마지막 수정날짜"),
                                            fieldWithPath("lsImageList[].name").type(JsonFieldType.STRING).description("이미지의 이름"),
                                            fieldWithPath("lsImageList[].tag").type(JsonFieldType.STRING).description("이미지의 태그"),
                                            fieldWithPath("lsImageList[].size").type(JsonFieldType.STRING).description("이미지의 크기")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("[Image][실패 테스트] 사용자가 전달한 인자가 형식에 맞지않다.")
    class LsImageFail {

        @Tag("controller")
        @Test
        @DisplayName("[Image][Controller] Ls Image")
        void 사용자가_전달한_인자가_형식에_맞지_않는경우() throws Exception {
            // given
            BDDMockito.given(imageService.lsImage(any(), any()))
                    .willThrow(new CustomBadRequestException(ErrorCode.ARGUMENT_COMMAND_NOT_VALID_EXCEPTION));

            // when
            ResultActions resultActions = mockMvc.perform(post("/api/v1/image/ls")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(writeValueAsStringRequest(List.of("tomcat -d")))
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
    @DisplayName("[Image][성공 테스트] 사용자가 DockerHub 에서 이미지를 받아온다")
    class PullImageSuccess {

        private PullImageResponseDto testResponse = null;

        @BeforeEach
        void setUpData() {
            // given
            testResponse = PullImageResponseDto.builder()
                    .pullImageFullName("tomcat:latest")
                    .build();
        }


        @Tag("controller")
        @Test
        @DisplayName("[Image][Controller] Pull Image")
        void DockerHub에서_사용자가_원하는_이미지를_받아온다_이미지_태그가_없는경우() throws Exception {
            // given
            BDDMockito.given(imageService.pullImage(any(), any()))
                    .willReturn(testResponse);

            // when
            ResultActions resultActions = mockMvc.perform(post("/api/v1/image/pull")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(writeValueAsStringRequest(List.of("tomcat")))
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
                                            fieldWithPath("pullImageFullName").type(JsonFieldType.STRING).description("Pull 받은 이미지의 FullName")
                                    )
                            )
                    );
        }

        @Tag("controller")
        @Test
        @DisplayName("[Image][Controller] Pull Image")
        void DockerHub에서_사용자가_원하는_이미지를_받아온다_이미지_태그가_있는경우() throws Exception {
            // given
            BDDMockito.given(imageService.pullImage(any(), any()))
                    .willReturn(testResponse);

            // when
            ResultActions resultActions = mockMvc.perform(post("/api/v1/image/pull")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(writeValueAsStringRequest(List.of("tomcat:latest")))
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
                                            fieldWithPath("pullImageFullName").type(JsonFieldType.STRING).description("Pull 받은 이미지의 FullName")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("[Image][실패 테스트] 사용자가 DockerHub 에서 이미지를 받아오는데 실패한다.")
    class PullImageFail {

        private PullImageResponseDto testResponse = null;

        @BeforeEach
        void setUpData() {
            // given
            testResponse = PullImageResponseDto.builder()
                    .pullImageFullName("tomcat:latest")
                    .build();
        }


        @Tag("controller")
        @Test
        @DisplayName("[Image][Controller] Pull Image")
        void DockerHub에서_사용자가_원하는_이미지가_없는경우() throws Exception {
            // given
            BDDMockito.given(imageService.pullImage(any(), any()))
                    .willThrow(new CustomBadRequestException(ErrorCode.NOT_EXIST_IMAGE_IN_DOCKERHUB));

            // when
            ResultActions resultActions = mockMvc.perform(post("/api/v1/image/pull")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(writeValueAsStringRequest(List.of("rest-docker")))
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
        @DisplayName("[Image][Controller] Pull Image")
        void 사용자가_전달한_인자가_형식에_맞지_않는경우() throws Exception {
            // given
            BDDMockito.given(imageService.pullImage(any(), any()))
                    .willThrow(new CustomBadRequestException(ErrorCode.ARGUMENT_COMMAND_NOT_VALID_EXCEPTION));

            // when
            ResultActions resultActions = mockMvc.perform(post("/api/v1/image/pull")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(writeValueAsStringRequest(List.of("tomcat mysql")))
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
    @DisplayName("[Image][성공 테스트] 사용자가 원하는 이미지의 자세한 정보를 응답한다")
    class InspectImageSuccess {

        private ImageDetailElements responseImageDetailElement = null;
        private InspectImageResponseDto testResponse = null;

        @BeforeEach
        void setUpData() {
            // given
            responseImageDetailElement = ImageDetailElements.builder()
                    .createDate(LocalDateTime.parse("2024-01-01T00:00:00"))
                    .updateDate(LocalDateTime.parse("2024-01-01T00:00:00"))
                    .name("tomcat")
                    .tag("latest")
                    .size("490702439")
                    .os("linux")
                    .architecture("arm64")
                    .build();

            testResponse = InspectImageResponseDto.builder()
                    .inspectImage(responseImageDetailElement)
                    .build();
        }


        @Tag("controller")
        @Test
        @DisplayName("[Image][Controller] Inspect Image")
        void 사용자가_원하는_이미지의_자세한_정보를_응답한다() throws Exception {
            // given
            BDDMockito.given(imageService.inspectImage(any(), any()))
                    .willReturn(testResponse);

            // when
            ResultActions resultActions = mockMvc.perform(post("/api/v1/image/inspect")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(writeValueAsStringRequest(List.of("tomcat")))
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
                                            fieldWithPath("inspectImage").type(JsonFieldType.OBJECT).description("반환된 이미지의 정보"),
                                            fieldWithPath("inspectImage.createDate").type(JsonFieldType.STRING).description("이미지의 생성날짜"),
                                            fieldWithPath("inspectImage.updateDate").type(JsonFieldType.STRING).description("이미지의 마지막 수정날짜"),
                                            fieldWithPath("inspectImage.name").type(JsonFieldType.STRING).description("이미지의 이름"),
                                            fieldWithPath("inspectImage.tag").type(JsonFieldType.STRING).description("이미지의 태그"),
                                            fieldWithPath("inspectImage.size").type(JsonFieldType.STRING).description("이미지의 크기"),
                                            fieldWithPath("inspectImage.os").type(JsonFieldType.STRING).description("이미지의 운영체저"),
                                            fieldWithPath("inspectImage.architecture").type(JsonFieldType.STRING).description("이미지의 아키텍처")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("[Image][실패 테스트] 사용자가 원하는 이미지의 자세한 정보를 응답하는데 실패한다")
    class InspectImageFail {


        @Tag("controller")
        @Test
        @DisplayName("[Image][Controller] Inspect Image")
        void 사용자가_이미지_인자를_전달하지_않은경우() throws Exception {
            // given
            BDDMockito.given(imageService.pullImage(any(), any()))
                    .willThrow(new CustomBadRequestException(ErrorCode.ARGUMENT_COMMAND_NOT_VALID_EXCEPTION));

            // when
            ResultActions resultActions = mockMvc.perform(post("/api/v1/image/inspect")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(writeValueAsStringRequest(List.of()))
            );

            // then
            resultActions
                    .andExpect(status().isOk())
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
        @DisplayName("[Image][Controller] Inspect Image")
        void 사용자가_전달한_인자가_형식에_맞지_않는경우() throws Exception {
            // given
            BDDMockito.given(imageService.inspectImage(any(), any()))
                    .willThrow(new CustomBadRequestException(ErrorCode.ARGUMENT_COMMAND_NOT_VALID_EXCEPTION));

            // when
            ResultActions resultActions = mockMvc.perform(post("/api/v1/image/inspect")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(writeValueAsStringRequest(List.of("tomcat mysql")))
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
        @DisplayName("[Image][Controller] Inspect Image")
        void 사용자가_원하는_이미지의_DB에_없는경우() throws Exception {
            // given
            BDDMockito.given(imageService.inspectImage(any(), any()))
                    .willReturn(InspectImageResponseDto.builder().build());

            // when
            ResultActions resultActions = mockMvc.perform(post("/api/v1/image/inspect")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(writeValueAsStringRequest(List.of("cattom")))
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
                                            fieldWithPath("inspectImage").type(JsonFieldType.NULL).description("반환된 이미지의 정보")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("[Image][성공 테스트] 사용자가 원하는 이미지를 삭제한다")
    class RmImageSuccess {

        private boolean testResult = true;
        private RmImageResponseDto testResponse = null;

        @BeforeEach
        void setUpData() {
            // given
            testResponse = RmImageResponseDto.builder()
                    .imageDeleteResult(testResult)
                    .build();
        }


        @Tag("controller")
        @Test
        @DisplayName("[Image][Controller] Inspect Image")
        void 사용자가_원하는_이미지를_삭제한다() throws Exception {
            // given
            BDDMockito.given(imageService.rmImage(any(), any()))
                    .willReturn(testResponse);

            // when
            ResultActions resultActions = mockMvc.perform(post("/api/v1/image/rm")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(writeValueAsStringRequest(List.of("tomcat")))
            );

            // then
            resultActions
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.imageDeleteResult").value(true))
                    .andDo(
                            restDocs.document(
                                    requestFields(
                                            fieldWithPath("argCommands").type(JsonFieldType.ARRAY).description("도커 명령어 인자값 리스트")
                                    ),
                                    responseFields(
                                            fieldWithPath("imageDeleteResult").type(JsonFieldType.BOOLEAN).description("반환된 이미지의 정보")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("[Image][실패 테스트] 사용자가 원하는 이미지를 삭제에 실패한다")
    class RmImageFail {

        private boolean testResult = false;
        private RmImageResponseDto testResponse = null;

        @BeforeEach
        void setUpData() {
            // given
            testResponse = RmImageResponseDto.builder()
                    .imageDeleteResult(testResult)
                    .build();
        }

        @Tag("controller")
        @Test
        @DisplayName("[Image][Controller] Inspect Image")
        void 사용자가_이미지를_인자로_전달하지_않은경우() throws Exception {
            // given
            BDDMockito.given(imageService.rmImage(any(), any()))
                    .willThrow(new CustomBadRequestException(ErrorCode.ARGUMENT_COMMAND_NOT_VALID_EXCEPTION));

            // when
            ResultActions resultActions = mockMvc.perform(post("/api/v1/image/rm")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(writeValueAsStringRequest(List.of()))
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
        @DisplayName("[Image][Controller] Inspect Image")
        void 사용자가_요청한_이미지의_삭제를_실패한경우() throws Exception {
            // given
            BDDMockito.given(imageService.rmImage(any(), any()))
                    .willReturn(testResponse);

            // when
            ResultActions resultActions = mockMvc.perform(post("/api/v1/image/rm")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(writeValueAsStringRequest(List.of("tomcat mysql")))
            );

            // then
            resultActions
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.imageDeleteResult").value(false))
                    .andDo(
                            restDocs.document(
                                    requestFields(
                                            fieldWithPath("argCommands").type(JsonFieldType.ARRAY).description("도커 명령어 인자값 리스트")
                                    ),
                                    responseFields(
                                            fieldWithPath("imageDeleteResult").type(JsonFieldType.BOOLEAN).description("반환된 이미지의 정보")
                                    )
                            )
                    );
        }
    }
}
