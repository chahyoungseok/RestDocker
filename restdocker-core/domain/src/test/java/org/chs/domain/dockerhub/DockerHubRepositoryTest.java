package org.chs.domain.dockerhub;

import org.chs.domain.common.structure.RepositoryTest;
import org.chs.domain.dockerhub.entity.DockerHubEntity;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.InvalidDataAccessApiUsageException;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class DockerHubRepositoryTest extends RepositoryTest {

    @Autowired
    private DockerHubEntityRepository dockerHubEntityRepository;

    DockerHubEntity pulledImage = null;

    @Nested
    @DisplayName("[DockerHub][성공/실패 테스트] DockerHub 를 조회한다.")
    class PullDockerImage {

        @BeforeEach
        void beforeEach() {
            pulledImage = DockerHubEntity.builder()
                    .name("tomcat")
                    .tag("latest")
                    .os("pulledImageOs")
                    .architecture("pulledImageArchitecture")
                    .size("pulledImageSize")
                    .build();

            pulledImage.setDateTimeForTest(LocalDateTime.now(), LocalDateTime.now());

            dockerHubEntityRepository.saveAndFlush(pulledImage);
        }

        @Tag("domain")
        @Test
        @DisplayName("[DockerHub][성공 테스트] Read DockerHub")
        void DockerHub에_있는_이미지를_정상적으로_조회한다() {
            // given
            String imageName = "tomcat:latest";

            // when
            DockerHubEntity testPulledResult = dockerHubEntityRepository
                    .selectDockerImage(imageName);

            // then
            assertThat(compareDockerHubEntity(testPulledResult, pulledImage));
        }

        @Tag("domain")
        @Test
        @DisplayName("[DockerHub][성공 테스트] Read DockerHub")
        void DockerHub에_있는_이미지를_tag없는_요청에도_정상적으로_조회한다() {
            // given
            String imageName = "tomcat";

            // when
            DockerHubEntity testPulledResult = dockerHubEntityRepository
                    .selectDockerImage(imageName);

            // then
            assertThat(compareDockerHubEntity(testPulledResult, pulledImage));
        }


        @Tag("domain")
        @Test
        @DisplayName("[DockerHub][실패 테스트] Read DockerHub")
        void DockerHub에_없는_이미지를_조회한다() {
            // given
            String imageName = "cattom";

            // when
            DockerHubEntity testPulledResult = dockerHubEntityRepository
                    .selectDockerImage(imageName);

            // then
            assertThat(testPulledResult).isNull();
        }

        @Tag("domain")
        @Test
        @DisplayName("[DockerHub][실패 테스트] Read DockerHub")
        void 이미지_Name에_콜론이_들어가_조회에_실패한다() {
            // given
            String imageName = "tomc:at:latest";

            // when && then
            Assertions.assertThrows(
                    InvalidDataAccessApiUsageException.class,
                    () -> dockerHubEntityRepository.selectDockerImage(imageName)
            );
        }
    }


    private boolean compareDockerHubEntity(DockerHubEntity compare1, DockerHubEntity compare2) {
        if (false == compare1.getName().equals(compare2.getName())
                || false == compare1.getTag().equals(compare2.getTag())
                || false == compare1.getOs().equals(compare2.getOs())
                || false == compare1.getArchitecture().equals(compare2.getArchitecture())
                || false == compare1.getSize().equals(compare2.getSize())) {
            return false;
        }

        return true;
    }
}
