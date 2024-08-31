package org.chs.restdockerapis.network.application;

import org.chs.domain.account.AccountRepository;
import org.chs.domain.account.entity.AccountEntity;
import org.chs.domain.common.enumerate.ThirdPartyEnum;
import org.chs.domain.container.ContainerEntityRepository;
import org.chs.domain.container.dto.ContainerElements;
import org.chs.domain.container.enumerate.ContainerStatusEnum;
import org.chs.domain.network.NetworkContainerMappingEntityRepository;
import org.chs.domain.network.NetworkEntityRepository;
import org.chs.domain.network.dto.NetworkDetailElements;
import org.chs.domain.network.dto.NetworkElements;
import org.chs.domain.network.entity.NetworkEntity;
import org.chs.restdockerapis.common.argument_resolver.dto.GetRequesterDto;
import org.chs.restdockerapis.common.exception.CustomBadRequestException;
import org.chs.restdockerapis.common.util.ListUtils;
import org.chs.restdockerapis.image.presentation.dto.DockerCommandRequestDto;
import org.chs.restdockerapis.network.presentation.dto.CreateNetworkResponseDto;
import org.chs.restdockerapis.network.presentation.dto.InspectNetworkResponseDto;
import org.chs.restdockerapis.network.presentation.dto.LsNetworkResponseDto;
import org.chs.restdockerapis.network.presentation.dto.RmNetworkResponseDto;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class NetworkServiceTest {

    @InjectMocks
    private NetworkService networkService;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private NetworkEntityRepository dockerNetworkRepository;

    @Mock
    private ContainerEntityRepository dockerContainerRepository;

    @Mock
    private NetworkContainerMappingEntityRepository dockerNetworkContainerMappingRepository;

    @Mock
    private ListUtils listUtils;

    @Mock
    private AddressUtils addressUtils;

    private GetRequesterDto testRequestInfo = null;
    private GetRequesterDto testInValidRequestInfo = null;

    private DockerCommandRequestDto testEmptyRequest = null;
    private DockerCommandRequestDto testNameRequest = null;
    private DockerCommandRequestDto testNameAndSubnetRequest = null;
    private DockerCommandRequestDto testNameAndSubnetAndGatewayRequest = null;
    private DockerCommandRequestDto testNameAndSubnetAndGatewayAndIpRangeRequest = null;
    private DockerCommandRequestDto testNameAndSubnetAndGatewayAndIpRangeAndOptionsRequest = null;

    public NetworkServiceTest() {
        testRequestInfo = GetRequesterDto.builder()
                .id("testId")
                .ipAddress("testIP")
                .oauthAccessToken("testOauthAccessToken")
                .oauthRefreshToken("testOauthRefreshToken")
                .thirdPartyType(ThirdPartyEnum.KAKAO)
                .build();

        testInValidRequestInfo = GetRequesterDto.builder()
                .id("testInValidId")
                .ipAddress("testIP")
                .oauthAccessToken("testOauthAccessToken")
                .oauthRefreshToken("testOauthRefreshToken")
                .thirdPartyType(ThirdPartyEnum.KAKAO)
                .build();

        testEmptyRequest = DockerCommandRequestDto.builder()
                .argCommands(List.of())
                .build();

        testNameRequest = DockerCommandRequestDto.builder()
                .argCommands(List.of("rest-docker"))
                .build();

        testNameAndSubnetRequest = DockerCommandRequestDto.builder()
                .argCommands(List.of("--subnet 172.17.0.0/16", "rest-docker"))
                .build();

        testNameAndSubnetAndGatewayRequest = DockerCommandRequestDto.builder()
                .argCommands(List.of("--gateway 172.17.0.1", "--subnet 172.17.0.0/16", "rest-docker"))
                .build();

        testNameAndSubnetAndGatewayAndIpRangeRequest = DockerCommandRequestDto.builder()
                .argCommands(List.of("--ip-range 172.17.0.0/24","--gateway 172.17.0.1", "--subnet 172.17.0.0/16", "rest-docker"))
                .build();

        testNameAndSubnetAndGatewayAndIpRangeAndOptionsRequest = DockerCommandRequestDto.builder()
                .argCommands(List.of("--opt com.docker.network.driver.mtu=1000", "--opt com.docker.network.bridge.enable_icc=true", "--ip-range 172.17.0.0/24","--gateway 172.17.0.1", "--subnet 172.17.0.0/16", "rest-docker"))
                .build();
    }

    @Nested
    @DisplayName("[Network][시나리오 테스트] 네트워크 조회(LS)를 테스트한다.")
    class LsNetwork {

        private NetworkElements networkElements = null;

        protected LsNetwork() {
            // given - data
            networkElements = NetworkElements.builder()
                    .createDate(LocalDateTime.parse("2024-01-01T00:00:00"))
                    .updateDate(LocalDateTime.parse("2024-01-01T00:00:00"))
                    .name("bridge")
                    .subnet("172.17.0.0/16")
                    .ipRange("172.17.0.0/24")
                    .gateway("172.17.0.1")
                    .build();
        }

        private boolean compareDto(NetworkElements elements1, NetworkElements elements2) {
            if (false == elements1.getName().equals(elements2.getName())
                    || false == elements1.getSubnet().equals(elements2.getSubnet())
                    || false == elements1.getIpRange().equals(elements2.getIpRange())
                    || false == elements1.getGateway().equals(elements2.getGateway())) {
                return false;
            }
            return true;
        }

        @Tag("business")
        @Test
        @DisplayName("[Network][Business] 도커 네트워크를 조회한다.")
        void 도커네트워크_조회를_정상적으로_성공한다() {
            // given - mocking
            BDDMockito.given(dockerNetworkRepository.findByOAuthServiceId(any()))
                    .willReturn(List.of(networkElements));

            BDDMockito.given(listUtils.isBlank(any()))
                    .willReturn(true);

            // when
            LsNetworkResponseDto actual = networkService.lsNetwork(testRequestInfo, testEmptyRequest);

            // then
            assertThat(compareDto(networkElements, actual.lsNetworkElements().get(0)));
        }

        @Tag("business")
        @Test
        @DisplayName("[Network][Business] 자세한 네트워크 시, 인자값의 개수가 유효하지 않아 실패한다.")
        void 자세한_네트워크_조회시_인자값의_개수가_유효하지_않아_실패한다() {
            // when & then
            Assertions.assertThrows(
                    CustomBadRequestException.class,
                    () -> networkService.lsNetwork(testRequestInfo, testNameRequest)
            );
        }
    }

    @Nested
    @DisplayName("[Network][시나리오 테스트] 자세한 네트워크 조회(Inspect)를 테스트한다.")
    class InspectNetwork {

        private NetworkDetailElements networkDetailElements = null;
        private ContainerElements containerElements = null;

        protected InspectNetwork() {
            // given - data
            networkDetailElements = NetworkDetailElements.builder()
                    .createDate(LocalDateTime.parse("2024-01-01T00:00:00"))
                    .updateDate(LocalDateTime.parse("2024-01-01T00:00:00"))
                    .name("bridge")
                    .subnet("172.17.0.0/16")
                    .ipRange("172.17.0.0/24")
                    .gateway("172.17.0.1")
                    .enableIcc(true)
                    .mtu(1000)
                    .build();

            containerElements = ContainerElements.builder()
                    .createDate(LocalDateTime.now())
                    .updateDate(LocalDateTime.now())
                    .name("restDocker")
                    .imageName("testImageName")
                    .imageTag("testImageTag")
                    .outerPort("1111")
                    .innerPort("2222")
                    .privateIp("172.17.18.11")
                    .status(ContainerStatusEnum.Running)
                    .build();
        }

        private boolean compareDto(NetworkDetailElements elements1, NetworkDetailElements elements2) {
            if (false == elements1.getName().equals(elements2.getName())
                    || false == elements1.getSubnet().equals(elements2.getSubnet())
                    || false == elements1.getIpRange().equals(elements2.getIpRange())
                    || false == elements1.getGateway().equals(elements2.getGateway())
                    || elements1.getEnableIcc() != elements2.getEnableIcc()
                    || elements1.getMtu() != elements2.getMtu()) {
                return false;
            }
            return true;
        }

        @Tag("business")
        @Test
        @DisplayName("[Network][Business] 자세한 네트워크 조회를 성공한다.")
        void 자세한_네트워크_조회를_정상적으로_성공한다() {
            // given - mocking
            BDDMockito.given(dockerNetworkRepository.inspectNetwork(any(), any()))
                    .willReturn(networkDetailElements);

            BDDMockito.given(dockerContainerRepository.lsContainer(any()))
                    .willReturn(List.of(containerElements));

            // when
            InspectNetworkResponseDto actual = networkService.inspectNetwork(testRequestInfo, testNameRequest);

            // then
            assertThat(compareDto(networkDetailElements, actual.inspectNetworkDetailElements()));
        }

        @Tag("business")
        @Test
        @DisplayName("[Network][Business] 자세한 네트워크 조회시, 인자값의 개수가 유효하지 않아 실패한다.")
        void 자세한_네트워크_조회시_인자값의_개수가_유효하지_않아_실패한다() {
            // when & then
            Assertions.assertThrows(
                    CustomBadRequestException.class,
                    () -> networkService.inspectNetwork(testRequestInfo, testEmptyRequest)
            );
        }

        @Tag("business")
        @Test
        @DisplayName("[Network][Business] 자세한 네트워크 조회시, 인자값의 개수가 많아 실패한다.")
        void 자세한_네트워크_조회시_인자값의_개수가_많아_실패한다() {
            // when & then
            Assertions.assertThrows(
                    CustomBadRequestException.class,
                    () -> networkService.inspectNetwork(testRequestInfo, testNameAndSubnetRequest)
            );
        }
    }

    @Nested
    @DisplayName("[Network][시나리오 테스트] 네트워크 생성을(Create)를 테스트한다.")
    class CreateNetwork {

        private NetworkDetailElements networkDetailElements = null;

        private AccountEntity testAccount = null;
        private NetworkEntity testNetwork = null;

        protected CreateNetwork() {
            // given - data
            networkDetailElements = NetworkDetailElements.builder()
                    .createDate(LocalDateTime.parse("2024-01-01T00:00:00"))
                    .updateDate(LocalDateTime.parse("2024-01-01T00:00:00"))
                    .name("bridge")
                    .subnet("172.17.0.0/16")
                    .ipRange("172.17.0.0/24")
                    .gateway("172.17.0.1")
                    .enableIcc(true)
                    .mtu(1000)
                    .build();

            testAccount = AccountEntity.builder()
                    .nickname("테스트용 계정1")
                    .oauthServiceId("testOAuthServiceId")
                    .thirdPartyType(ThirdPartyEnum.KAKAO)
                    .isActive(true)
                    .build();

            testNetwork = NetworkEntity.builder()
                    .name("bridge")
                    .subnet("172.17.0.0/16")
                    .ipRange("172.17.0.0/24")
                    .gateway("172.17.0.1")
                    .enableIcc(true)
                    .mtu(1500)
                    .account(testAccount)
                    .build();;
        }

        private boolean compareDto(NetworkDetailElements elements1, NetworkDetailElements elements2) {
            if (false == elements1.getName().equals(elements2.getName())
                    || false == elements1.getSubnet().equals(elements2.getSubnet())
                    || false == elements1.getIpRange().equals(elements2.getIpRange())
                    || false == elements1.getGateway().equals(elements2.getGateway())
                    || elements1.getEnableIcc() != elements2.getEnableIcc()
                    || elements1.getMtu() != elements2.getMtu()) {
                return false;
            }
            return true;
        }

        @Tag("business")
        @Test
        @DisplayName("[Network][Business] 네트워크 생성을 성공한다. - 전달인자 : Name")
        void 네트워크_생성을_성공한다_전달인자_Name() {
            // given - mocking
            BDDMockito.given(listUtils.isBlank(any()))
                    .willReturn(false);

            BDDMockito.given(addressUtils.automaticAllocationSubnet(any()))
                    .willReturn("172.18.0.0/16");

            BDDMockito.given(addressUtils.automaticAllocationGateway(any()))
                    .willReturn("172.18.0.1");

            BDDMockito.given(addressUtils.validAddressRangeFormat(any()))
                    .willReturn(true);

            BDDMockito.given(addressUtils.duplicateSubnetCheck(any(), any()))
                    .willReturn(false);

            BDDMockito.given(addressUtils.validAddressFormat(any()))
                    .willReturn(true);

            BDDMockito.given(addressUtils.validGatewayIntoSubnet(any(), any()))
                    .willReturn(true);

            BDDMockito.given(accountRepository.findByOauthServiceIdEqualsAndThirdPartyTypeEquals(any(), any()))
                    .willReturn(Optional.of(testAccount));

            BDDMockito.given(dockerNetworkRepository.save(any()))
                    .willReturn(testNetwork);

            // when
            CreateNetworkResponseDto actual = networkService.createNetwork(testRequestInfo, testNameRequest);

            // then
            Assertions.assertEquals("bridge", actual.networkName());
        }

        @Tag("business")
        @Test
        @DisplayName("[Network][Business] 네트워크 생성을 성공한다. - 전달인자 : Name, Subnet")
        void 네트워크_생성을_성공한다_전달인자_Name_Subnet() {
            // given - mocking
            BDDMockito.given(listUtils.isBlank(any()))
                    .willReturn(false);

            BDDMockito.given(addressUtils.validAddressRangeFormat(any()))
                    .willReturn(true);

            BDDMockito.given(addressUtils.duplicateSubnetCheck(any(), any()))
                    .willReturn(false);

            BDDMockito.given(accountRepository.findByOauthServiceIdEqualsAndThirdPartyTypeEquals(any(), any()))
                    .willReturn(Optional.of(testAccount));

            BDDMockito.given(dockerNetworkRepository.save(any()))
                    .willReturn(testNetwork);

            // when
            CreateNetworkResponseDto actual = networkService.createNetwork(testRequestInfo, testNameAndSubnetRequest);

            // then
            Assertions.assertEquals("bridge", actual.networkName());
        }

        @Tag("business")
        @Test
        @DisplayName("[Network][Business] 네트워크 생성을 성공한다. - 전달인자 : Name, Subnet, Gateway")
        void 네트워크_생성을_성공한다_전달인자_Name_Subnet_Gateway() {
            // given - mocking
            BDDMockito.given(listUtils.isBlank(any()))
                    .willReturn(false);

            BDDMockito.given(addressUtils.validAddressRangeFormat(any()))
                    .willReturn(true);

            BDDMockito.given(addressUtils.duplicateSubnetCheck(any(), any()))
                    .willReturn(false);

            BDDMockito.given(addressUtils.validAddressFormat(any()))
                    .willReturn(true);

            BDDMockito.given(addressUtils.validGatewayIntoSubnet(any(), any()))
                    .willReturn(true);

            BDDMockito.given(accountRepository.findByOauthServiceIdEqualsAndThirdPartyTypeEquals(any(), any()))
                    .willReturn(Optional.of(testAccount));

            BDDMockito.given(dockerNetworkRepository.save(any()))
                    .willReturn(testNetwork);

            // when
            CreateNetworkResponseDto actual = networkService.createNetwork(testRequestInfo, testNameAndSubnetAndGatewayRequest);

            // then
            Assertions.assertEquals("bridge", actual.networkName());
        }

        @Tag("business")
        @Test
        @DisplayName("[Network][Business] 네트워크 생성을 성공한다. - 전달인자 : Name, Subnet, Gateway, IPRange")
        void 네트워크_생성을_성공한다_전달인자_Name_Subnet_Gateway_IPRange() {
            // given - mocking
            BDDMockito.given(listUtils.isBlank(any()))
                    .willReturn(false);

            BDDMockito.given(addressUtils.validAddressRangeFormat(any()))
                    .willReturn(true);

            BDDMockito.given(addressUtils.duplicateSubnetCheck(any(), any()))
                    .willReturn(false);

            BDDMockito.given(addressUtils.validAddressFormat(any()))
                    .willReturn(true);

            BDDMockito.given(addressUtils.validGatewayIntoSubnet(any(), any()))
                    .willReturn(true);

            BDDMockito.given(addressUtils.validIPRangeIntoSubnet(any(), any()))
                    .willReturn(true);

            BDDMockito.given(accountRepository.findByOauthServiceIdEqualsAndThirdPartyTypeEquals(any(), any()))
                    .willReturn(Optional.of(testAccount));

            BDDMockito.given(dockerNetworkRepository.save(any()))
                    .willReturn(testNetwork);

            // when
            CreateNetworkResponseDto actual = networkService.createNetwork(testRequestInfo, testNameAndSubnetAndGatewayAndIpRangeRequest);

            // then
            Assertions.assertEquals("bridge", actual.networkName());
        }

        @Tag("business")
        @Test
        @DisplayName("[Network][Business] 네트워크 생성을 성공한다. - 전달인자 : Name, Subnet, Gateway, IPRange, Option")
        void 네트워크_생성을_성공한다_전달인자_Name_Subnet_Gateway_IPRange_Option() {
            // given - mocking
            BDDMockito.given(listUtils.isBlank(any()))
                    .willReturn(false);

            BDDMockito.given(addressUtils.validAddressRangeFormat(any()))
                    .willReturn(true);

            BDDMockito.given(addressUtils.duplicateSubnetCheck(any(), any()))
                    .willReturn(false);

            BDDMockito.given(addressUtils.validAddressFormat(any()))
                    .willReturn(true);

            BDDMockito.given(addressUtils.validGatewayIntoSubnet(any(), any()))
                    .willReturn(true);

            BDDMockito.given(addressUtils.validIPRangeIntoSubnet(any(), any()))
                    .willReturn(true);

            BDDMockito.given(accountRepository.findByOauthServiceIdEqualsAndThirdPartyTypeEquals(any(), any()))
                    .willReturn(Optional.of(testAccount));

            BDDMockito.given(dockerNetworkRepository.save(any()))
                    .willReturn(testNetwork);

            // when
            CreateNetworkResponseDto actual = networkService.createNetwork(testRequestInfo, testNameAndSubnetAndGatewayAndIpRangeAndOptionsRequest);

            // then
            Assertions.assertEquals("bridge", actual.networkName());
        }

        @Tag("business")
        @Test
        @DisplayName("[Network][Business] 네트워크 생성을 성공한다. - 전달인자 : Name")
        void 네트워크_이름에_띄어쓰기가_들어가있어_실패한다() {
            // given - mocking
            BDDMockito.given(listUtils.isBlank(any()))
                    .willReturn(false);

            BDDMockito.given(addressUtils.validAddressRangeFormat(any()))
                    .willReturn(true);

            BDDMockito.given(addressUtils.duplicateSubnetCheck(any(), any()))
                    .willReturn(false);

            BDDMockito.given(addressUtils.validAddressFormat(any()))
                    .willReturn(true);

            BDDMockito.given(addressUtils.validGatewayIntoSubnet(any(), any()))
                    .willReturn(true);

            BDDMockito.given(addressUtils.validIPRangeIntoSubnet(any(), any()))
                    .willReturn(true);

            BDDMockito.given(accountRepository.findByOauthServiceIdEqualsAndThirdPartyTypeEquals(any(), any()))
                    .willReturn(Optional.of(testAccount));

            BDDMockito.given(dockerNetworkRepository.save(any()))
                    .willReturn(testNetwork);

            // when
            CreateNetworkResponseDto actual = networkService.createNetwork(testRequestInfo, testNameAndSubnetAndGatewayAndIpRangeAndOptionsRequest);

            // then
            Assertions.assertEquals("bridge", actual.networkName());
        }
    }

    @Nested
    @DisplayName("[Network][시나리오 테스트] 네트워크 삭제(Rm)를 테스트한다.")
    class RmNetwork {

        boolean deleteNetworkResultSuccess = true;

        @Tag("business")
        @Test
        @DisplayName("[Network][Business] 네트워크 삭제를 정상적으로 성공한다.")
        void 네트워크_삭제를_정상적으로_성공한다() {
            // given - mocking
            BDDMockito.given(dockerNetworkRepository.rmNetwork(any(), any()))
                    .willReturn(deleteNetworkResultSuccess);

            BDDMockito.given(dockerNetworkContainerMappingRepository.existNetworkBindingContainer(any(), any()))
                    .willReturn(false);

            // when
            RmNetworkResponseDto actual = networkService.rmNetwork(testRequestInfo, testNameRequest);

            // then
            Assertions.assertEquals(true, actual.networkDeleteResult());
        }

        @Tag("business")
        @Test
        @DisplayName("[Network][Business] 네트워크 삭제 시, 인자값의 개수가 유효하지 않아 실패한다.")
        void 네트워크_삭제시_인자값의_개수가_유효하지_않아_실패한다() {
            // when & then
            Assertions.assertThrows(
                    CustomBadRequestException.class,
                    () -> networkService.rmNetwork(testRequestInfo, testEmptyRequest)
            );
        }

        @Tag("business")
        @Test
        @DisplayName("[Network][Business] 네트워크 삭제 시, 인자값의 개수가 많아 실패한다.")
        void 네트워크_삭제시_인자값의_개수가_많아_실패한다() {
            // when & then
            Assertions.assertThrows(
                    CustomBadRequestException.class,
                    () -> networkService.rmNetwork(testRequestInfo, testNameAndSubnetRequest)
            );
        }
    }
}
