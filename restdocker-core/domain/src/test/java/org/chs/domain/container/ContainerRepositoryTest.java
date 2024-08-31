package org.chs.domain.container;

import org.chs.domain.account.AccountRepository;
import org.chs.domain.account.entity.AccountEntity;
import org.chs.domain.common.enumerate.ThirdPartyEnum;
import org.chs.domain.common.structure.RepositoryTest;
import org.chs.domain.container.dto.ContainerDetailElements;
import org.chs.domain.container.dto.ContainerElements;
import org.chs.domain.container.dto.ContainerValidElementsDto;
import org.chs.domain.container.entity.ContainerEntity;
import org.chs.domain.container.enumerate.ContainerStatusEnum;
import org.chs.domain.image.ImageEntityRepository;
import org.chs.domain.image.entity.ImageEntity;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.InvalidDataAccessApiUsageException;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ContainerRepositoryTest extends RepositoryTest {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ImageEntityRepository imageEntityRepository;

    @Autowired
    private ContainerEntityRepository containerEntityRepository;

    private AccountEntity account;
    private ImageEntity image;
    private ContainerEntity container;

    @BeforeEach
    public void ContainerRepositoryTest() {
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

        accountRepository.save(account);
        imageEntityRepository.save(image);
        containerEntityRepository.save(container);
    }

    @Nested
    @DisplayName("[Container][성공/실패 테스트] OAuthServiceId 로 컨테이너들을 조회한다.")
    class LsContainer {

        private ContainerElements containerElements;

        @BeforeEach
        void LsContainer() {
            containerElements = ContainerElements.builder()
                    .name("RestDocker")
                    .imageName(image.getName())
                    .imageTag(image.getTag())
                    .privateIp("172.17.1.1")
                    .outerPort("18080")
                    .innerPort("8080")
                    .status(ContainerStatusEnum.Running)
                    .build();
        }

        private boolean compareContainerElement(ContainerElements elements1, ContainerElements elements2) {
            if (elements1.getPrivateIp().equals(elements2.getPrivateIp())
                    && elements1.getInnerPort().equals(elements2.getInnerPort())
                    && elements1.getOuterPort().equals(elements2.getOuterPort())
                    && elements1.getStatus().equals(elements2.getStatus())
                    && elements1.getName().equals(elements2.getName())
                    && elements1.getImageName().equals(elements2.getImageName())
                    && elements1.getImageTag().equals(elements2.getImageTag())) {
                return true;
            }
            return false;
        }

        @Tag("domain")
        @Test
        @DisplayName("[Container][성공 테스트] 데이터가 DB에 존재하여 ContainerElements를 반환한다.")
        void 데이터가_DB에_존재하여_true를_반환한다() {
            // given
            String oauthServiceId = "testOAuthServiceId";

            // when
            List<ContainerElements> actual = containerEntityRepository
                    .lsContainer(oauthServiceId);

            // then
            assertThat(compareContainerElement(containerElements, actual.get(0)));
        }

        @Tag("domain")
        @Test
        @DisplayName("[Container][성공 테스트] 데이터가 DB에 존재하지않아 비어있는 리스트를 반환한다.")
        void 데이터가_DB에_존재하지않아_false를_반환한다() {
            // given
            String oauthServiceId = "notExistOAuthServiceId";

            // when
            List<ContainerElements> actual = containerEntityRepository
                    .lsContainer(oauthServiceId);

            // then
            Assertions.assertEquals(List.of(), actual);
        }
    }

    @Nested
    @DisplayName("[Container][성공/실패 테스트] OAuthServiceId 와 containerName으로 컨테이너를 조회한다.")
    class InspectContainer {

        private ContainerDetailElements containerDetailElements;

        @BeforeEach
        void InspectContainer() {
            containerDetailElements = ContainerDetailElements.builder()
                    .name("RestDocker")
                    .imageName(image.getName())
                    .imageTag(image.getTag())
                    .privateIp("172.17.1.1")
                    .outerPort("18080")
                    .innerPort("8080")
                    .stopRm(true)
                    .status(ContainerStatusEnum.Running)
                    .build();
        }

        private boolean compareContainerDetailElement(ContainerDetailElements elements1, ContainerDetailElements elements2) {
            if (elements1.getPrivateIp().equals(elements2.getPrivateIp())
                    && elements1.getInnerPort().equals(elements2.getInnerPort())
                    && elements1.getOuterPort().equals(elements2.getOuterPort())
                    && elements1.getStatus().equals(elements2.getStatus())
                    && elements1.getName().equals(elements2.getName())
                    && elements1.getImageName().equals(elements2.getImageName())
                    && elements1.getImageTag().equals(elements2.getImageTag())
                    && elements1.isStopRm() == elements2.isStopRm()) {
                return true;
            }
            return false;
        }

        @Tag("domain")
        @Test
        @DisplayName("[Container][성공 테스트] 데이터가 DB에 존재하여 ContainerElements를 반환한다.")
        void 데이터가_DB에_존재하여_true를_반환한다() {
            // given
            String oauthServiceId = "testOAuthServiceId";
            String containerName = "RestDocker";

            // when
            ContainerDetailElements actual = containerEntityRepository
                    .inspectContainer(oauthServiceId, containerName);

            // then
            assertThat(compareContainerDetailElement(containerDetailElements, actual));
        }

        @Tag("domain")
        @Test
        @DisplayName("[Container][성공 테스트] 데이터가 DB에 존재하지않아 null을 반환한다.")
        void 데이터가_DB에_존재하지않아_null를_반환한다() {
            // given
            String oauthServiceId = "notExistOAuthServiceId";
            String containerName = "RestDocker";

            // when
            ContainerDetailElements actual = containerEntityRepository
                    .inspectContainer(oauthServiceId, containerName);

            // then
            Assertions.assertNull(actual);
        }

        @Tag("domain")
        @Test
        @DisplayName("[Container][실패 테스트] ContainerName 이 null이어서 에러를한다.")
        void ContainerName이_Null이어서_에러를_반환한다() {
            // given
            String oauthServiceId = "notExistOAuthServiceId";
            String containerName = null;

            // when && then
            Assertions.assertThrows(
                    InvalidDataAccessApiUsageException.class,
                    () -> containerEntityRepository.inspectContainer(oauthServiceId, containerName)
            );
        }
    }

    @Nested
    @DisplayName("[Container][성공/실패 테스트] 컨테이너 이름을 변경한다.")
    class RenameContainer {

        @Tag("domain")
        @Test
        @DisplayName("[Container][성공 테스트] 데이터가 DB에 존재하여 성공적으로 변경한다.")
        void 데이터가_DB에_존재하여_성공적으로_변경한다() {
            // given
            String containerPk = container.getPk();
            String postContainerName = "postContainerName";

            // when
            boolean actual = containerEntityRepository
                    .renameContainer(containerPk, postContainerName);

            // then
            Assertions.assertTrue(actual);
        }
    }

    @Nested
    @DisplayName("[Container][성공/실패 테스트] OAuthServiceId로 Container 정보를 반환한다.")
    class FindValidElementsListByOAuthServiceId {

        private ContainerValidElementsDto containerDetailElements;

        @BeforeEach
        void FindValidElementsListByOAuthServiceId() {
            containerDetailElements = ContainerValidElementsDto.builder()
                    .containerName("RestDocker")
                    .outerPort("18080")
                    .build();
        }

        private boolean compareContainerValidElements(ContainerValidElementsDto elements1, ContainerValidElementsDto elements2) {
            if (elements1.getContainerName().equals(elements2.getContainerName())
                    && elements1.getOuterPort().equals(elements2.getOuterPort())) {
                return true;
            }
            return false;
        }

        @Tag("domain")
        @Test
        @DisplayName("[Container][성공 테스트] 데이터가 DB에 존재하여 Container 정보를 성공적으로 반환한다.")
        void 데이터가_DB에_존재하여_Container_정보를_성공적으로_반환한다() {
            // given
            String oauthServiceId = "testOAuthServiceId";

            // when
            List<ContainerValidElementsDto> actual = containerEntityRepository
                    .findValidElementsListByOAuthServiceId(oauthServiceId);

            // then
            Assertions.assertTrue(compareContainerValidElements(containerDetailElements, actual.get(0)));
        }
    }

    @Nested
    @DisplayName("[Container][성공/실패 테스트] OAuthServiceId와 ContainerName으로 Container를 반환한다.")
    class FindContainerByOAuthServiceAndContainerName {

        private boolean compareContainerEntity(ContainerEntity entity1, ContainerEntity entity2) {
            if (entity1.getPrivateIp().equals(entity2.getPrivateIp())
                    && entity1.getInnerPort().equals(entity2.getInnerPort())
                    && entity1.getOuterPort().equals(entity2.getOuterPort())
                    && entity1.getStatus().equals(entity2.getStatus())
                    && entity1.getName().equals(entity2.getName())
                    && entity1.isStopRm() == entity2.isStopRm()) {
                return true;
            }
            return false;
        }

        @Tag("domain")
        @Test
        @DisplayName("[Container][성공 테스트] 데이터가 DB에 존재하여 Container를 성공적으로 반환한다.")
        void 데이터가_DB에_존재하여_Container를_성공적으로_반환한다() {
            // given
            String oauthServiceId = "testOAuthServiceId";
            String containerName = "RestDocker";

            // when
            ContainerEntity actual = containerEntityRepository
                    .findContainerByOAuthServiceAndContainerName(oauthServiceId, containerName);

            // then
            Assertions.assertTrue(compareContainerEntity(container, actual));
        }

        @Tag("domain")
        @Test
        @DisplayName("[Container][성공 테스트] 데이터가 DB에 존재하지않아 null을 반환한다.")
        void 데이터가_DB에_존재하지않아_null를_반환한다() {
            // given
            String oauthServiceId = "notExistOAuthServiceId";
            String containerName = "RestDocker";

            // when
            ContainerEntity actual = containerEntityRepository
                    .findContainerByOAuthServiceAndContainerName(oauthServiceId, containerName);

            // then
            Assertions.assertNull(actual);
        }
    }

    @Nested
    @DisplayName("[Container][성공/실패 테스트] 컨테이너 상태를 변경한다.")
    class UpdateContainerStatus {

        @Tag("domain")
        @Test
        @DisplayName("[Container][성공 테스트] 데이터가 DB에 존재하여 성공적으로 변경한다.")
        void 데이터가_DB에_존재하여_성공적으로_변경한다() {
            // given
            String containerPk = container.getPk();
            ContainerStatusEnum containerStatus = ContainerStatusEnum.Paused;

            // when
            long actual = containerEntityRepository
                    .updateContainerStatus(containerPk, containerStatus);

            // then
            Assertions.assertEquals(1L, actual);
        }

        @Tag("domain")
        @Test
        @DisplayName("[Container][실패 테스트] 데이터가 DB에 존재하지않아 변경에 실패한다.")
        void 데이터가_DB에_존재하지않아_변경에_실패한다() {
            // given
            String containerPk = container.getPk() + "@";
            ContainerStatusEnum containerStatus = ContainerStatusEnum.Paused;

            // when
            long actual = containerEntityRepository
                    .updateContainerStatus(containerPk, containerStatus);

            // then
            Assertions.assertEquals(0L, actual);
        }
    }

    @Nested
    @DisplayName("[Container][성공/실패 테스트] 컨테이너를 삭제한다.")
    class RmContainer {

        @Tag("domain")
        @Test
        @DisplayName("[Container][성공 테스트] 데이터가 DB에 존재하여 성공적으로 삭제한다.")
        void 데이터가_DB에_존재하여_성공적으로_삭제한다() {
            // given
            String containerPk = container.getPk();

            // when
            boolean actual = containerEntityRepository
                    .rmContainer(containerPk);

            // then
            Assertions.assertTrue(actual);
        }

        @Tag("domain")
        @Test
        @DisplayName("[Container][실패 테스트] 데이터가 DB에 존재하지않아 삭제에 실패한다.")
        void 데이터가_DB에_존재하지않아_변경에_실패한다() {
            // given
            String containerPk = container.getPk() + "@";

            // when
            boolean actual = containerEntityRepository
                    .rmContainer(containerPk);

            // then
            Assertions.assertFalse(actual);
        }
    }
}
