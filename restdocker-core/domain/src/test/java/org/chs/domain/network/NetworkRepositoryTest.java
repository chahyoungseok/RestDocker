package org.chs.domain.network;

import org.chs.domain.account.AccountRepository;
import org.chs.domain.account.entity.AccountEntity;
import org.chs.domain.common.enumerate.ThirdPartyEnum;
import org.chs.domain.common.structure.RepositoryTest;
import org.chs.domain.container.ContainerEntityRepository;
import org.chs.domain.network.dto.NetworkDetailElements;
import org.chs.domain.network.dto.NetworkElements;
import org.chs.domain.network.entity.NetworkEntity;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.InvalidDataAccessApiUsageException;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class NetworkRepositoryTest extends RepositoryTest {

    @Autowired
    private NetworkEntityRepository networkEntityRepository;

    @Autowired
    private ContainerEntityRepository containerEntityRepository;

    @Autowired
    private NetworkContainerMappingEntityRepository networkContainerMappingEntityRepository;

    @Autowired
    private AccountRepository accountRepository;

    private AccountEntity testAccount;
    private NetworkEntity testNetwork;

    @Nested
    @DisplayName("[Network][성공/실패 테스트] Docker Network 를 조회한다.")
    class LsDockerNetwork {

        @BeforeEach
        void LsDockerNetwork() {
            testAccount = AccountEntity.builder()
                    .nickname("테스트용 계정1")
                    .oauthServiceId("testOAuthServiceId")
                    .thirdPartyType(ThirdPartyEnum.KAKAO)
                    .isActive(true)
                    .build();

            testAccount.setDateTimeForTest(LocalDateTime.now(), LocalDateTime.now());
            accountRepository.save(testAccount);

            testNetwork = NetworkEntity.builder()
                    .name("bridge")
                    .subnet("172.17.0.0/16")
                    .ipRange("172.17.0.0/24")
                    .gateway("172.17.0.1")
                    .enableIcc(true)
                    .mtu(1500)
                    .account(testAccount)
                    .build();

            testNetwork.setDateTimeForTest(LocalDateTime.now(), LocalDateTime.now());
            networkEntityRepository.save(testNetwork);
        }

        private boolean compareEntityToDto(NetworkEntity network, NetworkElements networkElements) {
            if (false == network.getName().equals(networkElements.getName())
                    || false == network.getSubnet().equals(networkElements.getSubnet())
                    || false == network.getIpRange().equals(networkElements.getIpRange())
                    || false == network.getGateway().equals(networkElements.getGateway())) {
                return false;
            }
            return true;
        }

        @Tag("domain")
        @Test
        @DisplayName("[Network][성공 테스트] Ls Network - 네트워크 조회를 성공한다.")
        void 특정_네트워크_조회를_성공한다() {
            // when
            List<NetworkElements> actual = networkEntityRepository
                    .findByOAuthServiceId(testAccount.getOauthServiceId());

            // then
            assertThat(compareEntityToDto(testNetwork, actual.get(0)));
        }

        @Tag("domain")
        @Test
        @DisplayName("[Network][실패 테스트] Ls Network - OAuthServiceId이 Null인 경우 조회에 실패한다.")
        void OauthServiceId이_Null인_경우_조회에_실패한다() {
            // when && then
            Assertions.assertThrows(
                    InvalidDataAccessApiUsageException.class,
                    () -> networkEntityRepository.findByOAuthServiceId(null)
            );
        }
    }

    @Nested
    @DisplayName("[Network][성공/실패 테스트] 특정 Docker Network 를 자세히 조회한다.")
    class InspectDockerNetwork {

        @BeforeEach
        void InspectDockerNetwork() {
            testAccount = AccountEntity.builder()
                    .nickname("테스트용 계정1")
                    .oauthServiceId("testOAuthServiceId")
                    .thirdPartyType(ThirdPartyEnum.KAKAO)
                    .isActive(true)
                    .build();

            testAccount.setDateTimeForTest(LocalDateTime.now(), LocalDateTime.now());
            accountRepository.save(testAccount);

            testNetwork = NetworkEntity.builder()
                    .name("bridge")
                    .subnet("172.17.0.0/16")
                    .ipRange("172.17.0.0/24")
                    .gateway("172.17.0.1")
                    .enableIcc(true)
                    .mtu(1500)
                    .account(testAccount)
                    .build();

            testNetwork.setDateTimeForTest(LocalDateTime.now(), LocalDateTime.now());
            networkEntityRepository.save(testNetwork);
        }

        private boolean compareEntityToDto(NetworkEntity network, NetworkDetailElements networkDetailElements) {
            if (false == network.getName().equals(networkDetailElements.getName())
                    || false == network.getSubnet().equals(networkDetailElements.getSubnet())
                    || false == network.getIpRange().equals(networkDetailElements.getIpRange())
                    || false == network.getGateway().equals(networkDetailElements.getGateway())
                    || network.isEnableIcc() != networkDetailElements.getEnableIcc()
                    || network.getMtu() != networkDetailElements.getMtu()) {
                return false;
            }
            return true;
        }

        @Tag("domain")
        @Test
        @DisplayName("[Network][성공 테스트] Inspect Network - 특정 네트워크 조회를 성공한다.")
        void 특정_네트워크_조회를_성공한다() {
            // when
            NetworkDetailElements actual = networkEntityRepository
                    .inspectNetwork(testAccount.getOauthServiceId(), testNetwork.getName());

            // then
            assertThat(compareEntityToDto(testNetwork, actual));
        }

        @Tag("domain")
        @Test
        @DisplayName("[Network][실패 테스트] Inspect Network - OAuthServiceId이 Null인 경우 조회에 실패한다.")
        void OauthServiceId이_Null인_경우_조회에_실패한다() {
            // when && then
            Assertions.assertThrows(
                    InvalidDataAccessApiUsageException.class,
                    () -> networkEntityRepository.inspectNetwork(null, testNetwork.getName())
            );
        }
    }

    @Nested
    @DisplayName("[Network][성공/실패 테스트] 특정 Docker Network 를 삭제한다.")
    class RmDockerNetwork {

        @BeforeEach
        void RmDockerNetwork() {
            testAccount = AccountEntity.builder()
                    .nickname("테스트용 계정1")
                    .oauthServiceId("testOAuthServiceId")
                    .thirdPartyType(ThirdPartyEnum.KAKAO)
                    .isActive(true)
                    .build();

            testAccount.setDateTimeForTest(LocalDateTime.now(), LocalDateTime.now());
            accountRepository.save(testAccount);

            testNetwork = NetworkEntity.builder()
                    .name("bridge")
                    .subnet("172.17.0.0/16")
                    .ipRange("172.17.0.0/24")
                    .gateway("172.17.0.1")
                    .enableIcc(true)
                    .mtu(1500)
                    .account(testAccount)
                    .build();

            testNetwork.setDateTimeForTest(LocalDateTime.now(), LocalDateTime.now());
            networkEntityRepository.save(testNetwork);
        }

        @Tag("domain")
        @Test
        @DisplayName("[Network][성공 테스트] Rm Network - 특정 네트워크 삭제한다.")
        void 특정_네트워크_삭제를_성공한다() {
            // when
            boolean actual = networkEntityRepository
                    .rmNetwork(testAccount.getOauthServiceId(), testNetwork.getName());

            // then
            assertThat(actual).isEqualTo(true);
        }

        @Tag("domain")
        @Test
        @DisplayName("[Network][실패 테스트] Rm Network - OAuthServiceId이 Null인 경우 조회에 실패한다.")
        void OauthServiceId이_Null인_경우_조회에_실패한다() {
            // when && then
            Assertions.assertThrows(
                    InvalidDataAccessApiUsageException.class,
                    () -> networkEntityRepository.rmNetwork(null, testNetwork.getName())
            );
        }
    }
}
