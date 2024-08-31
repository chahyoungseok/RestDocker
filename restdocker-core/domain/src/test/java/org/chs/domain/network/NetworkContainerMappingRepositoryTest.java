package org.chs.domain.network;

import org.chs.domain.account.AccountRepository;
import org.chs.domain.account.entity.AccountEntity;
import org.chs.domain.common.enumerate.ThirdPartyEnum;
import org.chs.domain.common.structure.RepositoryTest;
import org.chs.domain.container.ContainerEntityRepository;
import org.chs.domain.container.entity.ContainerEntity;
import org.chs.domain.container.enumerate.ContainerStatusEnum;
import org.chs.domain.image.ImageEntityRepository;
import org.chs.domain.image.entity.ImageEntity;
import org.chs.domain.network.entity.NetworkContainerMappingEntity;
import org.chs.domain.network.entity.NetworkEntity;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

public class NetworkContainerMappingRepositoryTest extends RepositoryTest {

    @Autowired
    private NetworkContainerMappingEntityRepository networkContainerMappingRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ImageEntityRepository imageEntityRepository;

    @Autowired
    private NetworkEntityRepository networkEntityRepository;

    @Autowired
    private ContainerEntityRepository containerEntityRepository;

    private AccountEntity account;
    private ImageEntity image;
    private NetworkEntity network;
    private ContainerEntity container;
    private NetworkContainerMappingEntity networkContainerMapping;

    @BeforeEach
    public void NetworkContainerMappingRepositoryTest() {
        account = AccountEntity.builder()
                .thirdPartyAccessToken("testThirdPartyAccessToken")
                .thirdPartyRefreshToken("testThirdPartyRefreshToken")
                .nickname("testNickname")
                .oauthServiceId("testOAuthServiceId")
                .isActive(true)
                .thirdPartyType(ThirdPartyEnum.KAKAO)
                .build();
        account.setDateTimeForTest(LocalDateTime.now(), LocalDateTime.now());

        image = ImageEntity.builder()
                .name("testImageName")
                .os("testOs")
                .architecture("testArchitecture")
                .tag("testTag")
                .size("testSize")
                .account(account)
                .build();
        image.setDateTimeForTest(LocalDateTime.now(), LocalDateTime.now());

        container = ContainerEntity.builder()
                .name("RestDocker")
                .image(image)
                .privateIp("172.17.1.1")
                .outerPort("18080")
                .innerPort("8080")
                .status(ContainerStatusEnum.Running)
                .stopRm(true)
                .build();
        container.setDateTimeForTest(LocalDateTime.now(), LocalDateTime.now());

        network = NetworkEntity.builder()
                .name("bridge")
                .subnet("172.17.0.0/16")
                .ipRange("172.17.0.0/24")
                .gateway("172.17.0.1")
                .enableIcc(true)
                .mtu(1500)
                .account(account)
                .build();
        network.setDateTimeForTest(LocalDateTime.now(), LocalDateTime.now());

        networkContainerMapping = NetworkContainerMappingEntity.builder()
                .container(container)
                .network(network)
                .build();
        networkContainerMapping.setDateTimeForTest(LocalDateTime.now(), LocalDateTime.now());

        accountRepository.save(account);
        imageEntityRepository.save(image);
        containerEntityRepository.save(container);
        networkEntityRepository.save(network);
        networkContainerMappingRepository.save(networkContainerMapping);
    }

    @Nested
    @DisplayName("[NetworkContainerMapping][성공/실패 테스트] OAuthServiceId 와 networkName 으로 존재여부를 확인한다.")
    class ExistNetworkBindingContainer {

        @Tag("domain")
        @Test
        @DisplayName("[NetworkContainerMapping][성공 테스트] 데이터가 DB에 존재하여 true를 반환한다.")
        void 데이터가_DB에_존재하여_true를_반환한다() {
            // given
            String oauthServiceId = "testOAuthServiceId";
            String networkName = "bridge";

            // when
            boolean actual = networkContainerMappingRepository
                    .existNetworkBindingContainer(oauthServiceId, networkName);

            // then
            Assertions.assertEquals(true, actual);
        }

        @Tag("domain")
        @Test
        @DisplayName("[NetworkContainerMapping][성공 테스트] 데이터가 DB에 존재하지않아 false를 반환한다.")
        void 데이터가_DB에_존재하지않아_false를_반환한다() {
            // given
            String oauthServiceId = "notExistOAuthServiceId";
            String networkName = "bridge";

            // when
            boolean actual = networkContainerMappingRepository
                    .existNetworkBindingContainer(oauthServiceId, networkName);

            // then
            Assertions.assertEquals(false, actual);
        }
    }

    @Nested
    @DisplayName("[NetworkContainerMapping][성공/실패 테스트] OAuthServiceId 와 networkName 으로 조회한다.")
    class FindPrivateIpByOAuthServiceIdAndNetworkName {

        @Tag("domain")
        @Test
        @DisplayName("[NetworkContainerMapping][성공 테스트] 데이터가 DB에 존재하여 조회에 성공한다.")
        void 데이터가_DB에_존재하여_조회에_성공한다() {
            // given
            String oauthServiceId = "testOAuthServiceId";
            String networkName = "bridge";

            // when
            List<String> actual = networkContainerMappingRepository
                    .findPrivateIpByOAuthServiceIdAndNetworkName(oauthServiceId, networkName);

            // then
            Assertions.assertEquals(List.of("172.17.1.1"), actual);
        }

        @Tag("domain")
        @Test
        @DisplayName("[NetworkContainerMapping][성공 테스트] 데이터가 DB에 존재하지않아 비어있는 리스트를 반환한다.")
        void 데이터가_DB에_존재하지않아_비어있는_리스트를_반환한다() {
            // given
            String oauthServiceId = "notExistOAuthServiceId";
            String networkName = "bridge";

            // when
            List<String> actual = networkContainerMappingRepository
                    .findPrivateIpByOAuthServiceIdAndNetworkName(oauthServiceId, networkName);

            // then
            Assertions.assertEquals(List.of(), actual);
        }
    }
}
