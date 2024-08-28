package org.chs.restdockerapis.network.presentation;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.chs.domain.network.dto.NetworkDetailElements;
import org.chs.domain.network.dto.NetworkElements;
import org.chs.restdockerapis.common.exception.CustomBadRequestException;
import org.chs.restdockerapis.common.exception.ErrorCode;
import org.chs.restdockerapis.common.structure.ControllerTest;
import org.chs.restdockerapis.image.presentation.dto.DockerCommandRequestDto;
import org.chs.restdockerapis.network.application.NetworkService;
import org.chs.restdockerapis.network.presentation.dto.CreateNetworkResponseDto;
import org.chs.restdockerapis.network.presentation.dto.InspectNetworkResponseDto;
import org.chs.restdockerapis.network.presentation.dto.LsNetworkResponseDto;
import org.chs.restdockerapis.network.presentation.dto.RmNetworkResponseDto;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NetworkController.class)
public class NetworkControllerTest extends ControllerTest {

    @MockBean
    private NetworkService networkService;

    @Nested
    @DisplayName("[Network][성공 테스트] Network 조회를 테스트한다.")
    class LsNetworkSuccess {
        private List<NetworkElements> testNetworkElements = null;
        private LsNetworkResponseDto testResponse = null;

        @BeforeEach
        void setUpData() {
            // given
            testNetworkElements = List.of(
                    NetworkElements.builder()
                            .createDate(LocalDateTime.now())
                            .updateDate(LocalDateTime.now())
                            .name("bridge")
                            .subnet("172.17.1.0/16")
                            .ipRange("172.17.1.0/24")
                            .gateway("172.17.0.1")
                            .build()
            );

            testResponse = LsNetworkResponseDto.builder()
                    .lsNetworkElements(testNetworkElements)
                    .build();
        }

        private String makeRequest(List<String> argCommands) throws JsonProcessingException {
            return objectMapper.writeValueAsString(
                    DockerCommandRequestDto.builder()
                            .argCommands(argCommands)
                            .build()
            );
        }

        @Tag("controller")
        @Test
        @DisplayName("[Network][Controller] Ls Network")
        void 정상적으로_네트워크를_조회해온다() throws Exception {
            // given
            BDDMockito.given(networkService.lsNetwork(any(), any()))
                    .willReturn(testResponse);

            // when
            ResultActions resultActions = mockMvc.perform(post("/api/v1/network/ls")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(makeRequest(null))
            );

            // then
            resultActions
                    .andExpect(status().isOk())
                    .andDo(
                            restDocs.document(
                                    responseFields(
                                            fieldWithPath("lsNetworkElements").type(JsonFieldType.ARRAY).description("네트워크 요소들의 리스트"),
                                            fieldWithPath("lsNetworkElements[].createDate").type(JsonFieldType.STRING).description("네트워크 생성날짜"),
                                            fieldWithPath("lsNetworkElements[].updateDate").type(JsonFieldType.STRING).description("네트워크 수정날짜"),
                                            fieldWithPath("lsNetworkElements[].name").type(JsonFieldType.STRING).description("네트워크 이름"),
                                            fieldWithPath("lsNetworkElements[].subnet").type(JsonFieldType.STRING).description("네트워크 서브넷"),
                                            fieldWithPath("lsNetworkElements[].ipRange").type(JsonFieldType.STRING).description("네트워크 IP 대역"),
                                            fieldWithPath("lsNetworkElements[].gateway").type(JsonFieldType.STRING).description("네트워크 게이트웨이")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("[Network][실패 테스트] Network 조회를 테스트한다.")
    class LsNetworkFail {

        private List<NetworkElements> testNetworkElements = null;
        private LsNetworkResponseDto testResponse = null;

        @BeforeEach
        void setUpData() {
            // given
            testNetworkElements = List.of(
                    NetworkElements.builder()
                            .createDate(LocalDateTime.now())
                            .updateDate(LocalDateTime.now())
                            .name("bridge")
                            .subnet("172.17.1.0/16")
                            .ipRange("172.17.1.0/24")
                            .gateway("172.17.0.1")
                            .build()
            );

            testResponse = LsNetworkResponseDto.builder()
                    .lsNetworkElements(testNetworkElements)
                    .build();
        }

        private String makeRequest(List<String> argCommands) throws JsonProcessingException {
            return objectMapper.writeValueAsString(
                    DockerCommandRequestDto.builder()
                            .argCommands(argCommands)
                            .build()
            );
        }

        @Tag("controller")
        @Test
        @DisplayName("[Network][Controller] Ls Network")
        void 원하는_네트워크를_Ls로_조회하려다_실패한다() throws Exception {
            // given
            BDDMockito.given(networkService.lsNetwork(any(), any()))
                    .willThrow(new CustomBadRequestException(ErrorCode.ARGUMENT_COMMAND_NOT_VALID_EXCEPTION));

            // when
            ResultActions resultActions = mockMvc.perform(post("/api/v1/network/ls")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(makeRequest(List.of("bridge")))
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
    @DisplayName("[Network][성공 테스트] Network 자세한 조회를 테스트한다.")
    class InspectNetworkSuccess {
        private NetworkDetailElements testNetworkDetailElement = null;
        private InspectNetworkResponseDto testResponse = null;

        @BeforeEach
        void setUpData() {
            // given
            testNetworkDetailElement = NetworkDetailElements.builder()
                    .createDate(LocalDateTime.now())
                    .updateDate(LocalDateTime.now())
                    .name("bridge")
                    .subnet("172.17.1.0/16")
                    .ipRange("172.17.1.0/24")
                    .gateway("172.17.0.1")
                    .enableIcc(true)
                    .mtu(1000)
                    .build();

            testResponse = InspectNetworkResponseDto.builder()
                    .inspectNetworkDetailElements(testNetworkDetailElement)
                    .build();
        }

        private String makeRequest(List<String> argCommands) throws JsonProcessingException {
            return objectMapper.writeValueAsString(
                    DockerCommandRequestDto.builder()
                            .argCommands(argCommands)
                            .build()
            );
        }

        @Tag("controller")
        @Test
        @DisplayName("[Network][Controller] Inspect Network")
        void 정상적으로_네트워크를_조회해온다() throws Exception {
            // given
            BDDMockito.given(networkService.inspectNetwork(any(), any()))
                    .willReturn(testResponse);

            // when
            ResultActions resultActions = mockMvc.perform(post("/api/v1/network/inspect")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(makeRequest(List.of("bridge")))
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
                                            fieldWithPath("inspectNetworkDetailElements").type(JsonFieldType.OBJECT).description("네트워크 요소들의 리스트"),
                                            fieldWithPath("inspectNetworkDetailElements.createDate").type(JsonFieldType.STRING).description("네트워크 생성날짜"),
                                            fieldWithPath("inspectNetworkDetailElements.updateDate").type(JsonFieldType.STRING).description("네트워크 수정날짜"),
                                            fieldWithPath("inspectNetworkDetailElements.name").type(JsonFieldType.STRING).description("네트워크 이름"),
                                            fieldWithPath("inspectNetworkDetailElements.subnet").type(JsonFieldType.STRING).description("네트워크 서브넷"),
                                            fieldWithPath("inspectNetworkDetailElements.ipRange").type(JsonFieldType.STRING).description("네트워크 IP 대역"),
                                            fieldWithPath("inspectNetworkDetailElements.gateway").type(JsonFieldType.STRING).description("네트워크 게이트웨이"),
                                            fieldWithPath("inspectNetworkDetailElements.enableIcc").type(JsonFieldType.BOOLEAN).description("컨테이너 간 통신이 가능하게 할지의 여부"),
                                            fieldWithPath("inspectNetworkDetailElements.mtu").type(JsonFieldType.NUMBER).description("네트워크 인터페이스가 한번에 전송할 수 있는 최대 데이터 패킷 크기")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("[Network][실패 테스트] Network 자세한 조회를 테스트한다.")
    class InspectNetworkFail {

        private DockerCommandRequestDto testRequest = null;

        private String makeRequest(List<String> argCommands) throws JsonProcessingException {
            return objectMapper.writeValueAsString(
                    DockerCommandRequestDto.builder()
                            .argCommands(argCommands)
                            .build()
            );
        }

        @Tag("controller")
        @Test
        @DisplayName("[Network][Controller] Ls Network")
        void 네트워크_Inspect_조회_시_특정_네트워크를_지정하지_않아_실패한다() throws Exception {
            // given
            BDDMockito.given(networkService.inspectNetwork(any(), any()))
                    .willThrow(new CustomBadRequestException(ErrorCode.ARGUMENT_COMMAND_NOT_VALID_EXCEPTION));

            // when
            ResultActions resultActions = mockMvc.perform(post("/api/v1/network/inspect")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(makeRequest(List.of()))
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
    @DisplayName("[Network][성공 테스트] Network 생성을 테스트한다.")
    class CreateNetworkSuccess {
        private CreateNetworkResponseDto testResponse = null;

        @BeforeEach
        void setUpData() {
            // given
            testResponse = CreateNetworkResponseDto.builder()
                    .networkName("restdocker")
                    .build();
        }

        private String makeRequest(List<String> argCommands) throws JsonProcessingException {
            return objectMapper.writeValueAsString(
                    DockerCommandRequestDto.builder()
                            .argCommands(argCommands)
                            .build()
            );
        }

        @Tag("controller")
        @Test
        @DisplayName("[Network][Controller] Create Network")
        void 네트워크_이름만으로_정상적인_네트워크를_생성한다() throws Exception {
            // given
            BDDMockito.given(networkService.createNetwork(any(), any()))
                    .willReturn(testResponse);

            // when
            ResultActions resultActions = mockMvc.perform(post("/api/v1/network/create")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(makeRequest(List.of("restdocker")))
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
                                            fieldWithPath("networkName").type(JsonFieldType.STRING).description("생성한 네트워크 이름")
                                    )
                            )
                    );
        }

        @Tag("controller")
        @Test
        @DisplayName("[Network][Controller] Create Network")
        void 네트워크_이름과_서브넷으로_정상적인_네트워크를_생성한다() throws Exception {
            // given
            BDDMockito.given(networkService.createNetwork(any(), any()))
                    .willReturn(testResponse);

            // when
            ResultActions resultActions = mockMvc.perform(post("/api/v1/network/create")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(makeRequest(List.of("restdocker", "--subnet 172.17.0.0/16")))
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
                                            fieldWithPath("networkName").type(JsonFieldType.STRING).description("생성한 네트워크 이름")
                                    )
                            )
                    );
        }

        @Tag("controller")
        @Test
        @DisplayName("[Network][Controller] Create Network")
        void 네트워크_이름과_서브넷과_게이트웨이로_정상적인_네트워크를_생성한다() throws Exception {
            // given
            BDDMockito.given(networkService.createNetwork(any(), any()))
                    .willReturn(testResponse);

            // when
            ResultActions resultActions = mockMvc.perform(post("/api/v1/network/create")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(makeRequest(List.of("restdocker", "--subnet 172.17.0.0/16", "--gateway 172.17.0.1")))
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
                                            fieldWithPath("networkName").type(JsonFieldType.STRING).description("생성한 네트워크 이름")
                                    )
                            )
                    );
        }

        @Tag("controller")
        @Test
        @DisplayName("[Network][Controller] Create Network")
        void 네트워크_이름과_서브넷과_게이트웨이와_IP대역으로_정상적인_네트워크를_생성한다() throws Exception {
            // given
            BDDMockito.given(networkService.createNetwork(any(), any()))
                    .willReturn(testResponse);

            // when
            ResultActions resultActions = mockMvc.perform(post("/api/v1/network/create")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(makeRequest(List.of("restdocker", "--subnet 172.17.0.0/16", "--gateway 172.17.0.1", "--ip-range 172.17.0.0/24")))
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
                                            fieldWithPath("networkName").type(JsonFieldType.STRING).description("생성한 네트워크 이름")
                                    )
                            )
                    );
        }

        @Tag("controller")
        @Test
        @DisplayName("[Network][Controller] Create Network")
        void 네트워크_이름과_서브넷과_게이트웨이와_IP대역과_옵션들로_정상적인_네트워크를_생성한다() throws Exception {
            // given
            BDDMockito.given(networkService.createNetwork(any(), any()))
                    .willReturn(testResponse);

            // when
            ResultActions resultActions = mockMvc.perform(post("/api/v1/network/create")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(makeRequest(
                            List.of(
                                    "restdocker",
                                    "--subnet 172.17.0.0/16",
                                    "--gateway 172.17.0.1",
                                    "--ip-range 172.17.0.0/24",
                                    "--opt com.docker.network.bridge.enable_icc=true",
                                    "--opt com.docker.network.driver.mtu=1000"
                            )))
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
                                            fieldWithPath("networkName").type(JsonFieldType.STRING).description("생성한 네트워크 이름")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("[Network][실패 테스트] Network 생성을 테스트한다.")
    class CreateNetworkFail {

        private String makeRequest(List<String> argCommands) throws JsonProcessingException {
            return objectMapper.writeValueAsString(
                    DockerCommandRequestDto.builder()
                            .argCommands(argCommands)
                            .build()
            );
        }

        @Tag("controller")
        @Test
        @DisplayName("[Network][Controller] Create Network")
        void 네트워크_생성시_네트워크_이름을_지정하지_않아_실패한다() throws Exception {
            // given
            BDDMockito.given(networkService.createNetwork(any(), any()))
                    .willThrow(new CustomBadRequestException(ErrorCode.ARGUMENT_COMMAND_NOT_VALID_EXCEPTION));

            // when
            ResultActions resultActions = mockMvc.perform(post("/api/v1/network/create")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(makeRequest(List.of()))
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
        @DisplayName("[Network][Controller] Create Network")
        void 네트워크_생성시_네트워크_이름에_띄어쓰기가_포함되어_실패한다() throws Exception {
            // given
            BDDMockito.given(networkService.createNetwork(any(), any()))
                    .willThrow(new CustomBadRequestException(ErrorCode.ARGUMENT_COMMAND_NOT_VALID_EXCEPTION));

            // when
            ResultActions resultActions = mockMvc.perform(post("/api/v1/network/create")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(makeRequest(List.of("rest docker")))
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
        @DisplayName("[Network][Controller] Create Network")
        void 네트워크_생성시_서브넷이_겹치게되어_실패한다() throws Exception {
            // given
            BDDMockito.given(networkService.createNetwork(any(), any()))
                    .willThrow(new CustomBadRequestException(ErrorCode.DUPLICATE_SUBNET));

            // when
            ResultActions resultActions = mockMvc.perform(post("/api/v1/network/create")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(makeRequest(List.of("restdocker", "--subnet 172.17.0.0/16")))
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
        @DisplayName("[Network][Controller] Create Network")
        void 네트워크_생성시_서브넷이_유효하지않아_실패한다() throws Exception {
            // given
            BDDMockito.given(networkService.createNetwork(any(), any()))
                    .willThrow(new CustomBadRequestException(ErrorCode.NOT_VALID_ADDRESS_FORMAT));

            // when
            ResultActions resultActions = mockMvc.perform(post("/api/v1/network/create")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(makeRequest(List.of("restdocker", "--subnet 172.17.0.0/33")))
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
        @DisplayName("[Network][Controller] Create Network")
        void 네트워크_생성시_서브넷이_시작주소가_아니어서_실패한다() throws Exception {
            // given
            BDDMockito.given(networkService.createNetwork(any(), any()))
                    .willThrow(new CustomBadRequestException(ErrorCode.NOT_VALID_ADDRESS_FORMAT));

            // when
            ResultActions resultActions = mockMvc.perform(post("/api/v1/network/create")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(makeRequest(List.of("restdocker", "--subnet 172.17.0.1/16")))
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
    @DisplayName("[Network][성공 테스트] Network 삭제를 테스트한다.")
    class RmNetworkSuccess {
        private RmNetworkResponseDto testResponse = null;

        @BeforeEach
        void setUpData() {
            // given
            testResponse = RmNetworkResponseDto.builder()
                    .networkDeleteResult(true)
                    .build();
        }

        private String makeRequest(List<String> argCommands) throws JsonProcessingException {
            return objectMapper.writeValueAsString(
                    DockerCommandRequestDto.builder()
                            .argCommands(argCommands)
                            .build()
            );
        }

        @Tag("controller")
        @Test
        @DisplayName("[Network][Controller] Rm Network")
        void 정상적으로_네트워크를_삭제한다() throws Exception {
            // given
            BDDMockito.given(networkService.rmNetwork(any(), any()))
                    .willReturn(testResponse);

            // when
            ResultActions resultActions = mockMvc.perform(post("/api/v1/network/rm")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(makeRequest(List.of("bridge")))
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
                                            fieldWithPath("networkDeleteResult").type(JsonFieldType.BOOLEAN).description("삭제 성공 여부")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("[Network][실패 테스트] Network 자세한 조회를 테스트한다.")
    class RmNetworkFail {

        private DockerCommandRequestDto testRequest = null;

        private String makeRequest(List<String> argCommands) throws JsonProcessingException {
            return objectMapper.writeValueAsString(
                    DockerCommandRequestDto.builder()
                            .argCommands(argCommands)
                            .build()
            );
        }

        @Tag("controller")
        @Test
        @DisplayName("[Network][Controller] Rm Network")
        void 네트워크_삭제시_특정_네트워크를_지정하지_않아_실패한다() throws Exception {
            // given
            BDDMockito.given(networkService.rmNetwork(any(), any()))
                    .willThrow(new CustomBadRequestException(ErrorCode.ARGUMENT_COMMAND_NOT_VALID_EXCEPTION));

            // when
            ResultActions resultActions = mockMvc.perform(post("/api/v1/network/rm")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(makeRequest(List.of()))
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
}
