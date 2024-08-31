package org.chs.restdockerapis.container.application;

import org.chs.domain.account.entity.AccountEntity;
import org.chs.domain.common.enumerate.ThirdPartyEnum;
import org.chs.domain.container.ContainerEntityRepository;
import org.chs.domain.container.dto.ContainerDetailElements;
import org.chs.domain.container.dto.ContainerElements;
import org.chs.domain.container.dto.ContainerValidElementsDto;
import org.chs.domain.container.entity.ContainerEntity;
import org.chs.domain.container.enumerate.ContainerStatusEnum;
import org.chs.domain.image.ImageEntityRepository;
import org.chs.domain.image.entity.ImageEntity;
import org.chs.domain.network.NetworkContainerMappingEntityRepository;
import org.chs.domain.network.NetworkEntityRepository;
import org.chs.domain.network.dto.NetworkDetailElements;
import org.chs.domain.network.entity.NetworkEntity;
import org.chs.restdockerapis.common.argument_resolver.dto.GetRequesterDto;
import org.chs.restdockerapis.common.exception.CustomBadRequestException;
import org.chs.restdockerapis.common.exception.ErrorCode;
import org.chs.restdockerapis.container.presentation.dto.*;
import org.chs.restdockerapis.image.presentation.dto.DockerCommandRequestDto;
import org.chs.restdockerapis.network.util.AddressUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class ContainerServiceTest {

    @InjectMocks
    private ContainerService containerService;

    @Mock
    private ImageEntityRepository dockerImageRepository;

    @Mock
    private NetworkEntityRepository dockerNetworkRepository;

    @Mock
    private ContainerEntityRepository dockerContainerRepository;

    @Mock
    private NetworkContainerMappingEntityRepository dockerNetworkContainerMappingRepository;

    @Mock
    private AddressUtils addressUtils;

    private AccountEntity account;
    private ImageEntity image;
    private NetworkEntity network;
    private ContainerEntity container;
    private ContainerEntity pauseContainer;
    private ContainerEntity notRmContainer;

    private GetRequesterDto testRequestInfo = null;

    private DockerCommandRequestDto testEmptyRequest = null;
    private DockerCommandRequestDto testNameRequest = null;
    private DockerCommandRequestDto testNameArgRequest = null;


    public ContainerServiceTest() {
        account = AccountEntity.builder()
                .thirdPartyAccessToken("testThirdPartyAccessToken")
                .thirdPartyRefreshToken("testThirdPartyRefreshToken")
                .nickname("testNickname")
                .oauthServiceId("testOAuthServiceId")
                .isActive(true)
                .build();

        image = ImageEntity.builder()
                .name("testImageName")
                .os("testOs")
                .architecture("testArchitecture")
                .tag("testTag")
                .size("testSize")
                .account(account)
                .build();

        container = ContainerEntity.builder()
                .name("RestDocker")
                .image(image)
                .privateIp("172.17.1.2")
                .outerPort("18080")
                .innerPort("8080")
                .status(ContainerStatusEnum.Running)
                .stopRm(true)
                .build();

        pauseContainer = ContainerEntity.builder()
                .name("RestDocker")
                .image(image)
                .privateIp("172.17.1.2")
                .outerPort("18080")
                .innerPort("8080")
                .status(ContainerStatusEnum.Paused)
                .stopRm(true)
                .build();

        notRmContainer = ContainerEntity.builder()
                .name("RestDocker")
                .image(image)
                .privateIp("172.17.1.2")
                .outerPort("18080")
                .innerPort("8080")
                .status(ContainerStatusEnum.Running)
                .stopRm(false)
                .build();

        network = NetworkEntity.builder()
                .name("bridge")
                .subnet("172.17.0.0/16")
                .ipRange("172.17.0.0/24")
                .gateway("172.17.0.1")
                .enableIcc(true)
                .mtu(1500)
                .account(account)
                .build();

        testRequestInfo = GetRequesterDto.builder()
                .id("testId")
                .ipAddress("testIP")
                .oauthAccessToken("testOauthAccessToken")
                .oauthRefreshToken("testOauthRefreshToken")
                .thirdPartyType(ThirdPartyEnum.KAKAO)
                .build();

        testEmptyRequest = DockerCommandRequestDto.builder()
                .argCommands(List.of())
                .build();

        testNameRequest = DockerCommandRequestDto.builder()
                .argCommands(List.of("RestDocker"))
                .build();

        testNameArgRequest = DockerCommandRequestDto.builder()
                .argCommands(List.of("RestDocker", "-a"))
                .build();
    }


    @Nested
    @DisplayName("[Container][시나리오 테스트] 컨테이너 조회(LS)를 테스트한다.")
    class LSContainer {

        private DockerCommandRequestDto testLsRequest = null;

        private ContainerElements testContainerElements = null;
        private LsContainerResponseDto testResponse = null;

        protected LSContainer() {
            // given - data
            testLsRequest = DockerCommandRequestDto.builder()
                    .argCommands(List.of("-l", "-a"))
                    .build();

            testContainerElements = ContainerElements.builder()
                    .createDate(LocalDateTime.parse("2024-01-01T00:00:00"))
                    .updateDate(LocalDateTime.parse("2024-01-01T00:00:00"))
                    .name("RestDocker")
                    .imageName("tomcat")
                    .imageTag("latest")
                    .privateIp("172.17.1.1")
                    .outerPort("18080")
                    .innerPort("8080")
                    .status(ContainerStatusEnum.Running)
                    .build();

            testResponse = LsContainerResponseDto.builder()
                    .containerElementsList(List.of(testContainerElements))
                    .build();
        }

        @Tag("business")
        @Test
        @DisplayName("[Container][Business] 인자가 전달되지 않았을 때 조회를 정상적으로 성공한다.")
        void 인자가_전달되지_않았을_때_조회를_정상적으로_성공한다() {
            // given - mocking
            BDDMockito.given(dockerContainerRepository.lsContainer(any()))
                    .willReturn(List.of(testContainerElements));

            // when
            LsContainerResponseDto actual = containerService.lsContainer(testRequestInfo, testEmptyRequest);

            // then
            Assertions.assertEquals(testResponse.containerElementsList(), actual.containerElementsList());
        }

        @Tag("business")
        @Test
        @DisplayName("[Container][Business] 인자가 전달되었을 때 조회를 정상적으로 성공한다.")
        void 인자가_전달되었을_때_조회를_정상적으로_성공한다() {
            // given - mocking
            BDDMockito.given(dockerContainerRepository.lsContainer(any()))
                    .willReturn(List.of(testContainerElements));

            // when
            LsContainerResponseDto actual = containerService.lsContainer(testRequestInfo, testLsRequest);

            // then
            Assertions.assertEquals(testResponse.containerElementsList(), actual.containerElementsList());
        }

        @Tag("business")
        @Test
        @DisplayName("[Container][Business] 유효하지 않은 인자가 전달되었을 때 조회를 정상적으로 실패한다.")
        void 유효하지_않은_인자가_전달되었을_때_조회를_정상적으로_성공한다() {
            // given - mocking
            BDDMockito.given(dockerContainerRepository.lsContainer(any()))
                    .willThrow(new CustomBadRequestException(ErrorCode.ARGUMENT_COMMAND_NOT_VALID_EXCEPTION));

            // when && then
            Assertions.assertThrows(
                    CustomBadRequestException.class,
                    () -> containerService.lsContainer(testRequestInfo, testLsRequest)
            );
        }
    }

    @Nested
    @DisplayName("[Container][시나리오 테스트] 컨테이너 자세한 조회(Inspect)를 테스트한다.")
    class InspectContainer {

        private ContainerDetailElements testContainerDetailElements = null;
        private InspectContainerResponseDto testResponse = null;

        protected InspectContainer() {
            // given - data
            testContainerDetailElements = ContainerDetailElements.builder()
                    .createDate(LocalDateTime.parse("2024-01-01T00:00:00"))
                    .updateDate(LocalDateTime.parse("2024-01-01T00:00:00"))
                    .name("RestDocker")
                    .imageName("tomcat")
                    .imageTag("latest")
                    .privateIp("172.17.1.1")
                    .outerPort("18080")
                    .innerPort("8080")
                    .stopRm(true)
                    .status(ContainerStatusEnum.Running)
                    .build();

            testResponse = InspectContainerResponseDto.builder()
                    .inspectContainerDetailElements(testContainerDetailElements)
                    .build();
        }

        @Tag("business")
        @Test
        @DisplayName("[Container][Business] 조회할 컨테이너 이름이 전달되었을 때 정상적으로 성공한다.")
        void 조회할_컨테이너_이름이_전달되었을_때_조회를_정상적으로_성공한다() {
            // given - mocking
            BDDMockito.given(dockerContainerRepository.inspectContainer(any(), any()))
                    .willReturn(testContainerDetailElements);

            // when
            InspectContainerResponseDto actual = containerService.inspectContainer(testRequestInfo, testNameRequest);

            // then
            Assertions.assertEquals(testResponse.inspectContainerDetailElements(), actual.inspectContainerDetailElements());
        }

        @Tag("business")
        @Test
        @DisplayName("[Container][Business] 컨테이너 이름 외에 다른 추가인자가 전달되었을 때 실패한다.")
        void 컨테이너_이름_외에_다른_추가인자가_전달되었을_때_실패한다() {
            // when && then
            Assertions.assertThrows(
                    CustomBadRequestException.class,
                    () -> containerService.inspectContainer(testRequestInfo, testNameArgRequest)
            );
        }

        @Tag("business")
        @Test
        @DisplayName("[Container][Business] 인자가 전달되지 않았을 때 조회를 정상적으로 실패한다.")
        void 인자가_전달되지_않았을_때_조회를_정상적으로_실패한다() {
            // when && then
            Assertions.assertThrows(
                    CustomBadRequestException.class,
                    () -> containerService.inspectContainer(testRequestInfo, testEmptyRequest)
            );
        }
    }

    @Nested
    @DisplayName("[Container][시나리오 테스트] 컨테이너 이름변경을 테스트한다.")
    class RenameContainer {

        private DockerCommandRequestDto testRenameRequest = null;

        private RenameContainerResponseDto testResponse = null;

        protected RenameContainer() {
            // given - data
            testRenameRequest = DockerCommandRequestDto.builder()
                    .argCommands(List.of("RestDocker", "RestApiDocker"))
                    .build();

            testResponse = RenameContainerResponseDto.builder()
                    .renameResult(true)
                    .build();
        }

        @Tag("business")
        @Test
        @DisplayName("[Container][Business] 변경전 컨테이너 이름과 변경할 컨테이너 이름만 전달되었을 때 정상적으로 성공한다.")
        void 변경전_컨테이너_이름과_변경할_컨테이너_이름만_전달되었을_때_정상적으로_성공한다() {
            // given - mocking
            BDDMockito.given(dockerContainerRepository.findContainerByOAuthServiceAndContainerName(any(), any()))
                    .willReturn(container);

            BDDMockito.given(dockerContainerRepository.renameContainer(any(), any()))
                    .willReturn(true);

            // when
            RenameContainerResponseDto actual = containerService.renameContainer(testRequestInfo, testRenameRequest);

            // then
            Assertions.assertEquals(testResponse.renameResult(), actual.renameResult());
        }

        @Tag("business")
        @Test
        @DisplayName("[Container][Business] 변경 전후 컨테이너 이름이 오지 않았을 때 정상적으로 실패한다.")
        void 변경_전후_컨테이너_이름이_오지_않았을_때_정상적으로_실패한다() {
            // when && then
            Assertions.assertThrows(
                    CustomBadRequestException.class,
                    () -> containerService.renameContainer(testRequestInfo, testNameRequest)
            );
        }

        @Tag("business")
        @Test
        @DisplayName("[Container][Business] 인자가 전달되지 않았을 때 조회를 정상적으로 실패한다.")
        void 인자가_전달되지_않았을_때_조회를_정상적으로_실패한다() {
            // when && then
            Assertions.assertThrows(
                    CustomBadRequestException.class,
                    () -> containerService.renameContainer(testRequestInfo, testEmptyRequest)
            );
        }
    }

    @Nested
    @DisplayName("[Container][시나리오 테스트] 컨테이너 생성을 테스트한다.")
    class CreateContainer {
        private ContainerElements containerElements = null;
        private NetworkDetailElements networkDetailElements = null;
        private ContainerValidElementsDto containerValidElements = null;

        private CreateContainerResponseDto testResponse = null;

        protected CreateContainer() {
            // given - data
            containerElements = ContainerElements.builder()
                    .createDate(LocalDateTime.now())
                    .updateDate(LocalDateTime.now())
                    .outerPort("18080")
                    .innerPort("8080")
                    .status(ContainerStatusEnum.Running)
                    .privateIp("172.17.1.1")
                    .name("existContainerName")
                    .imageName("tomcat")
                    .imageTag("latest")
                    .build();

            networkDetailElements = NetworkDetailElements.builder()
                    .createDate(LocalDateTime.now())
                    .updateDate(LocalDateTime.now())
                    .subnet("172.17.0.0/16")
                    .ipRange("172.17.0.0/24")
                    .gateway("172.17.0.1")
                    .enableIcc(true)
                    .mtu(1500)
                    .name("bridge")
                    .containerInfo(List.of(containerElements))
                    .build();

            containerValidElements = ContainerValidElementsDto.builder()
                    .containerName("existContainerName")
                    .outerPort("18080")
                    .build();

            testResponse = CreateContainerResponseDto.builder()
                    .containerName("RestDocker")
                    .build();
        }

        @Tag("business")
        @Test
        @DisplayName("[Container][Business] 컨테이너 이름과 이미지 이름만 전달되었을 때 정상적으로 성공한다.")
        void 컨테이너_이름과_이미지_이름만_전달되었을_때_정상적으로_성공한다() {
            // given - data
            DockerCommandRequestDto testContainerNameAndImageName = DockerCommandRequestDto.builder()
                    .argCommands(
                            List.of(
                                    "--name createdContainerName",
                                    "containerName:latest"
                            )
                    )
                    .build();

            // given - mocking
            BDDMockito.given(dockerContainerRepository.findValidElementsListByOAuthServiceId(any()))
                    .willReturn(List.of(containerValidElements));

            BDDMockito.given(dockerNetworkRepository.inspectNetwork(any(), any()))
                    .willReturn(networkDetailElements);

            BDDMockito.given(addressUtils.automaticAllocationContainerIp(any(), any()))
                    .willReturn("172.17.1.2");

            BDDMockito.given(addressUtils.validAddressFormat(any()))
                    .willReturn(true);

            BDDMockito.given(dockerNetworkContainerMappingRepository.findPrivateIpByOAuthServiceIdAndNetworkName(any(), any()))
                    .willReturn(List.of("172.17.1.1"));

            BDDMockito.given(dockerImageRepository.findByOAuthServiceIdAndImageFullName(any(), any()))
                    .willReturn(image);

            BDDMockito.given(dockerNetworkRepository.findByOAuthServiceIdAndNetworkName(any(), any()))
                    .willReturn(network);

            BDDMockito.given(dockerContainerRepository.save(any()))
                    .willReturn(container);

            // when
            CreateContainerResponseDto actual = containerService.createContainer(testRequestInfo, testContainerNameAndImageName);

            // then
            Assertions.assertEquals(testResponse.containerName(), actual.containerName());
        }

        @Tag("business")
        @Test
        @DisplayName("[Container][Business] 컨테이너 이름과 이미지 이름과 네트워크만 전달되었을 때 정상적으로 성공한다.")
        void 컨테이너이름_이미지이름_네트워크만_전달되었을_때_정상적으로_성공한다() {
            // given - data
            DockerCommandRequestDto testContainerNameAndImageNameAndNetworkName = DockerCommandRequestDto.builder()
                    .argCommands(
                            List.of(
                                    "--name createdContainerName",
                                    "--net bridge",
                                    "containerName:latest"
                            )
                    )
                    .build();

            // given - mocking
            BDDMockito.given(dockerContainerRepository.findValidElementsListByOAuthServiceId(any()))
                    .willReturn(List.of(containerValidElements));

            BDDMockito.given(dockerNetworkRepository.inspectNetwork(any(), any()))
                    .willReturn(networkDetailElements);

            BDDMockito.given(addressUtils.automaticAllocationContainerIp(any(), any()))
                    .willReturn("172.17.1.2");

            BDDMockito.given(addressUtils.validAddressFormat(any()))
                    .willReturn(true);

            BDDMockito.given(dockerNetworkContainerMappingRepository.findPrivateIpByOAuthServiceIdAndNetworkName(any(), any()))
                    .willReturn(List.of("172.17.1.1"));

            BDDMockito.given(dockerImageRepository.findByOAuthServiceIdAndImageFullName(any(), any()))
                    .willReturn(image);

            BDDMockito.given(dockerNetworkRepository.findByOAuthServiceIdAndNetworkName(any(), any()))
                    .willReturn(network);

            BDDMockito.given(dockerContainerRepository.save(any()))
                    .willReturn(container);

            // when
            CreateContainerResponseDto actual = containerService.createContainer(testRequestInfo, testContainerNameAndImageNameAndNetworkName);

            // then
            Assertions.assertEquals(testResponse.containerName(), actual.containerName());
        }

        @Tag("business")
        @Test
        @DisplayName("[Container][Business] 컨테이너 이름과 이미지 이름과 네트워크와 내부 IP가 전달되었을 때 정상적으로 성공한다.")
        void 컨테이너이름_이미지이름_네트워크와_내부IP가_전달되었을_때_정상적으로_성공한다() {
            // given - data
            DockerCommandRequestDto testContainerNameAndImageNameAndNetworkNameAndPrivateIp = DockerCommandRequestDto.builder()
                    .argCommands(
                            List.of(
                                    "--name createdContainerName",
                                    "-rm",
                                    "--net bridge",
                                    "--ip 172.17.1.2",
                                    "containerName:latest"
                            )
                    )
                    .build();

            // given - mocking
            BDDMockito.given(dockerContainerRepository.findValidElementsListByOAuthServiceId(any()))
                    .willReturn(List.of(containerValidElements));

            BDDMockito.given(addressUtils.validAddressFormat(any()))
                    .willReturn(true);

            BDDMockito.given(dockerNetworkContainerMappingRepository.findPrivateIpByOAuthServiceIdAndNetworkName(any(), any()))
                    .willReturn(List.of("172.17.1.1"));

            BDDMockito.given(dockerImageRepository.findByOAuthServiceIdAndImageFullName(any(), any()))
                    .willReturn(image);

            BDDMockito.given(dockerNetworkRepository.findByOAuthServiceIdAndNetworkName(any(), any()))
                    .willReturn(network);

            BDDMockito.given(dockerContainerRepository.save(any()))
                    .willReturn(container);

            // when
            CreateContainerResponseDto actual = containerService.createContainer(testRequestInfo, testContainerNameAndImageNameAndNetworkNameAndPrivateIp);

            // then
            Assertions.assertEquals(testResponse.containerName(), actual.containerName());
        }

        @Tag("business")
        @Test
        @DisplayName("[Container][Business] 모든인자가 전달되었을 때 정상적으로 성공한다.")
        void 모든인자가_전달되었을_때_정상적으로_성공한다() {
            // given -data
            DockerCommandRequestDto testAllArg = DockerCommandRequestDto.builder()
                    .argCommands(
                            List.of(
                                    "--name createdContainerName",
                                    "-rm",
                                    "--net bridge",
                                    "--ip 172.17.1.2",
                                    "-p 18081:8080",
                                    "containerName:latest"
                            )
                    )
                    .build();

            // given - mocking
            BDDMockito.given(dockerContainerRepository.findValidElementsListByOAuthServiceId(any()))
                    .willReturn(List.of(containerValidElements));

            BDDMockito.given(addressUtils.validAddressFormat(any()))
                    .willReturn(true);

            BDDMockito.given(dockerNetworkContainerMappingRepository.findPrivateIpByOAuthServiceIdAndNetworkName(any(), any()))
                    .willReturn(List.of("172.17.1.1"));

            BDDMockito.given(addressUtils.validPortForwardingFormat(any()))
                    .willReturn(true);

            BDDMockito.given(dockerImageRepository.findByOAuthServiceIdAndImageFullName(any(), any()))
                    .willReturn(image);

            BDDMockito.given(dockerNetworkRepository.findByOAuthServiceIdAndNetworkName(any(), any()))
                    .willReturn(network);

            BDDMockito.given(dockerContainerRepository.save(any()))
                    .willReturn(container);

            // when
            CreateContainerResponseDto actual = containerService.createContainer(testRequestInfo, testAllArg);

            // then
            Assertions.assertEquals(testResponse.containerName(), actual.containerName());
        }

        @Tag("business")
        @Test
        @DisplayName("[Container][Business] 네트워크가 명시적 선언되지 않았는데 내부 IP가 선언되었다면 정상적으로 실패한다.")
        void 네트워크가_명시적선언_되지_않았는데_내부IP가_선언되었다면_정상적으로_실패한다() {
            // given -data
            DockerCommandRequestDto testInvalidArg = DockerCommandRequestDto.builder()
                    .argCommands(
                            List.of(
                                    "--name createdContainerName",
                                    "--ip 172.17.1.2",
                                    "containerName:latest"
                            )
                    )
                    .build();

            // when && then
            Assertions.assertThrows(
                    CustomBadRequestException.class,
                    () -> containerService.createContainer(testRequestInfo, testInvalidArg)
            );
        }

        @Tag("business")
        @Test
        @DisplayName("[Container][Business] Host에 이미 있는 컨테이너 이름으로 요청을 보내면 정상적으로 실패한다.")
        void Host에_이미_있는_컨테이너_이름으로_요청을보내면_정상적으로_실패한다() {
            // given -data
            DockerCommandRequestDto testInvalidArg = DockerCommandRequestDto.builder()
                    .argCommands(
                            List.of(
                                    "--name existContainerName",
                                    "containerName:latest"
                            )
                    )
                    .build();

            // given - mocking
            BDDMockito.given(dockerContainerRepository.findValidElementsListByOAuthServiceId(any()))
                    .willReturn(List.of(containerValidElements));

            // when && then
            Assertions.assertThrows(
                    CustomBadRequestException.class,
                    () -> containerService.createContainer(testRequestInfo, testInvalidArg)
            );
        }

        @Tag("business")
        @Test
        @DisplayName("[Container][Business] DockerHost에 이미 할당된 내부 IP를 요청으로 보내면 정상적으로 실패한다.")
        void DockerHost에_이미_할당된_내부IP를_요청으로보내면_정상적으로_실패한다() {
            // given -data
            DockerCommandRequestDto testInvalidArg = DockerCommandRequestDto.builder()
                    .argCommands(
                            List.of(
                                    "--name containerName",
                                    "--net bridge",
                                    "--ip 172.17.1.1",
                                    "containerName:latest"
                            )
                    )
                    .build();

            // given - mocking
            BDDMockito.given(dockerContainerRepository.findValidElementsListByOAuthServiceId(any()))
                    .willReturn(List.of(containerValidElements));

            BDDMockito.given(addressUtils.validAddressFormat(any()))
                    .willReturn(true);

            // when && then
            Assertions.assertThrows(
                    CustomBadRequestException.class,
                    () -> containerService.createContainer(testRequestInfo, testInvalidArg)
            );
        }

        @Tag("business")
        @Test
        @DisplayName("[Container][Business] Host에 이미 할당된 외부 port 를 요청으로 보내면 정상적으로 실패한다.")
        void Host에_이미_할당된_외부Port를_요청으로보내면_정상적으로_실패한다() {
            // given -data
            DockerCommandRequestDto testInvalidArg = DockerCommandRequestDto.builder()
                    .argCommands(
                            List.of(
                                    "--name containerName",
                                    "--net bridge",
                                    "--ip 172.17.1.1",
                                    "-p 18080:8080",
                                    "containerName:latest"
                            )
                    )
                    .build();

            // given - mocking
            BDDMockito.given(dockerContainerRepository.findValidElementsListByOAuthServiceId(any()))
                    .willReturn(List.of(containerValidElements));

            BDDMockito.given(addressUtils.validAddressFormat(any()))
                    .willReturn(true);

            BDDMockito.given(addressUtils.validPortForwardingFormat(any()))
                    .willReturn(true);


            // when && then
            Assertions.assertThrows(
                    CustomBadRequestException.class,
                    () -> containerService.createContainer(testRequestInfo, testInvalidArg)
            );
        }
    }

    @Nested
    @DisplayName("[Container][시나리오 테스트] 컨테이너 삭제를 테스트한다.")
    class RmContainer {

        private RmContainerResponseDto testResponse = null;

        protected RmContainer() {
            // given - data
            testResponse = RmContainerResponseDto.builder()
                    .rmResult(true)
                    .build();
        }

        @Tag("business")
        @Test
        @DisplayName("[Container][Business] 컨테이너 이름만 전달되었을 때 정상적으로 성공한다.")
        void 컨테이너_이름만_전달되었을_때_정상적으로_성공한다() {
            // given - mocking
            BDDMockito.given(dockerContainerRepository.findContainerByOAuthServiceAndContainerName(any(), any()))
                    .willReturn(container);

            BDDMockito.given(dockerContainerRepository.rmContainer(any()))
                    .willReturn(true);

            // when
            RmContainerResponseDto actual = containerService.rmContainer(testRequestInfo.id(), testNameRequest);

            // then
            Assertions.assertEquals(testResponse.rmResult(), actual.rmResult());
        }

        @Tag("business")
        @Test
        @DisplayName("[Container][Business] 컨테이너 이름외에 추가인자가 왔을경우 정상적으로 실패한다.")
        void 컨테이너_이름외에_추가인자가_왔을경우_정상적으로_실패한다() {
            // when && then
            Assertions.assertThrows(
                    CustomBadRequestException.class,
                    () -> containerService.rmContainer(testRequestInfo.id(), testNameArgRequest)
            );
        }

        @Tag("business")
        @Test
        @DisplayName("[Container][Business] 요청으로 온 컨테이너 이름의 정보가 없을경우 정상적으로 실패한다.")
        void 요청으로_온_컨테이너_이름의_정보가_없을경우_정상적으로_실패한다() {
            // given - mocking
            BDDMockito.given(dockerContainerRepository.findContainerByOAuthServiceAndContainerName(any(), any()))
                    .willThrow(new CustomBadRequestException(ErrorCode.NOT_EXIST_CONTAINER));

            // when && then
            Assertions.assertThrows(
                    CustomBadRequestException.class,
                    () -> containerService.rmContainer(testRequestInfo.id(), testNameRequest)
            );
        }
    }

    @Nested
    @DisplayName("[Container][시나리오 테스트] 컨테이너 시작을 테스트한다.")
    class StartContainer {

        private StartContainerResponseDto testResponse = null;

        protected StartContainer() {
            // given - data
            testResponse = StartContainerResponseDto.builder()
                    .startResult(true)
                    .build();
        }

        @Tag("business")
        @Test
        @DisplayName("[Container][Business] 컨테이너 이름만 전달되었을 때 정상적으로 성공한다.")
        void 컨테이너_이름만_전달되었을_때_정상적으로_성공한다() {
            // given - mocking
            BDDMockito.given(dockerContainerRepository.findContainerByOAuthServiceAndContainerName(any(), any()))
                    .willReturn(pauseContainer);

            BDDMockito.given(dockerContainerRepository.updateContainerStatus(any(), any()))
                    .willReturn(1L);

            // when
            StartContainerResponseDto actual = containerService.startContainer(testRequestInfo.id(), testNameRequest);

            // then
            Assertions.assertEquals(testResponse.startResult(), actual.startResult());
        }

        @Tag("business")
        @Test
        @DisplayName("[Container][Business] 컨테이너 이름외에 추가인자가 왔을경우 정상적으로 실패한다.")
        void 컨테이너_이름외에_추가인자가_왔을경우_정상적으로_실패한다() {
            // when && then
            Assertions.assertThrows(
                    CustomBadRequestException.class,
                    () -> containerService.startContainer(testRequestInfo.id(), testNameArgRequest)
            );
        }

        @Tag("business")
        @Test
        @DisplayName("[Container][Business] 요청으로 온 컨테이너가 Running 상태인경우 정상적으로 실패한다.")
        void 요청으로_온_컨테이너가_Running_상태인경우_정상적으로_실패한다() {
            // given - mocking
            BDDMockito.given(dockerContainerRepository.findContainerByOAuthServiceAndContainerName(any(), any()))
                    .willReturn(container);

            // when && then
            Assertions.assertThrows(
                    CustomBadRequestException.class,
                    () -> containerService.startContainer(testRequestInfo.id(), testNameRequest)
            );
        }
    }

    @Nested
    @DisplayName("[Container][시나리오 테스트] 컨테이너 중지를 테스트한다.")
    class StopContainer {

        private StopContainerResponseDto testResponse = null;

        protected StopContainer() {
            // given - data
            testResponse = StopContainerResponseDto.builder()
                    .stopResult(true)
                    .build();
        }

        @Tag("business")
        @Test
        @DisplayName("[Container][Business] 컨테이너 이름만 전달되었을 때 중지를 정상적으로 성공한다. - stopRm = false")
        void 컨테이너_이름만_전달되었을_때_중지를_정상적으로_성공한다_StopRm이_false인경우() {
            // given - mocking
            BDDMockito.given(dockerContainerRepository.findContainerByOAuthServiceAndContainerName(any(), any()))
                    .willReturn(notRmContainer);

            BDDMockito.given(dockerContainerRepository.updateContainerStatus(any(), any()))
                    .willReturn(1L);

            // when
            StopContainerResponseDto actual = containerService.stopContainer(testRequestInfo.id(), testNameRequest);

            // then
            Assertions.assertEquals(testResponse.stopResult(), actual.stopResult());
        }

        @Tag("business")
        @Test
        @DisplayName("[Container][Business] 컨테이너 이름만 전달되었을 때 중지를 정상적으로 성공한다. - stopRm = true")
        void 컨테이너_이름만_전달되었을_때_중지를_정상적으로_성공한다_StopRm이_true인경우() {
            // given - mocking
            BDDMockito.given(dockerContainerRepository.findContainerByOAuthServiceAndContainerName(any(), any()))
                    .willReturn(container);

            BDDMockito.given(dockerContainerRepository.findContainerByOAuthServiceAndContainerName(any(), any()))
                    .willReturn(container);

            BDDMockito.given(dockerContainerRepository.rmContainer(any()))
                    .willReturn(true);


            // when
            StopContainerResponseDto actual = containerService.stopContainer(testRequestInfo.id(), testNameRequest);

            // then
            Assertions.assertEquals(testResponse.stopResult(), actual.stopResult());
        }

        @Tag("business")
        @Test
        @DisplayName("[Container][Business] 컨테이너 이름외에 추가인자가 왔을경우 정상적으로 실패한다.")
        void 요청으로_온_컨테이너_Running_상태가_아닌경우_정상적으로_실패한다() {
            // given - mocking
            BDDMockito.given(dockerContainerRepository.findContainerByOAuthServiceAndContainerName(any(), any()))
                    .willReturn(pauseContainer);

            // when && then
            Assertions.assertThrows(
                    CustomBadRequestException.class,
                    () -> containerService.stopContainer(testRequestInfo.id(), testNameRequest)
            );
        }
        @Tag("business")
        @Test
        @DisplayName("[Container][Business] 컨테이너 이름외에 추가인자가 왔을경우 정상적으로 실패한다.")
        void 컨테이너_이름외에_추가인자가_왔을경우_정상적으로_실패한다() {
            // when && then
            Assertions.assertThrows(
                    CustomBadRequestException.class,
                    () -> containerService.stopContainer(testRequestInfo.id(), testNameArgRequest)
            );
        }
    }
}
