package org.chs.domain.image;

import org.chs.domain.account.AccountRepository;
import org.chs.domain.account.entity.AccountEntity;
import org.chs.domain.common.enumerate.ThirdPartyEnum;
import org.chs.domain.common.structure.RepositoryTest;
import org.chs.domain.image.dto.ImageDetailElements;
import org.chs.domain.image.dto.ImageElements;
import org.chs.domain.image.entity.ImageEntity;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.InvalidDataAccessApiUsageException;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ImageRepositoryTest extends RepositoryTest {

    @Autowired
    private ImageEntityRepository imageEntityRepository;

    @Autowired
    private AccountRepository accountRepository;

    private AccountEntity testAccount;
    private ImageEntity testImage;

    @Nested
    @DisplayName("[Image][성공/실패 테스트] Docker Image 를 조회한다.")
    class LsDockerImage {

        @BeforeEach
        void LsDockerImage() {
            testAccount = AccountEntity.builder()
                    .nickname("테스트용 계정1")
                    .oauthServiceId("testOauthServiceId")
                    .thirdPartyType(ThirdPartyEnum.KAKAO)
                    .isActive(true)
                    .build();

            testAccount.setDateTimeForTest(LocalDateTime.now(), LocalDateTime.now());
            accountRepository.save(testAccount);

            testImage = ImageEntity.builder()
                    .name("tomcat")
                    .tag("latest")
                    .os("pulledImageOs")
                    .architecture("pulledImageArchitecture")
                    .size("pulledImageSize")
                    .account(testAccount)
                    .build();

            testImage.setDateTimeForTest(LocalDateTime.now(), LocalDateTime.now());
            imageEntityRepository.save(testImage);
        }

        private boolean compareEntityAndDto(ImageEntity imageEntity, ImageElements imageDto) {
            if (false == imageEntity.getName().equals(imageDto.getName())
                    || false == imageEntity.getTag().equals(imageDto.getTag())
                    || false == imageEntity.getCreateDate().equals(imageDto.getCreateDate())
                    || false == imageEntity.getUpdateDate().equals(imageDto.getUpdateDate())
                    || false == imageEntity.getSize().equals(imageDto.getSize())) {
                return false;
            }
            return true;
        }

        @Tag("domain")
        @Test
        @DisplayName("[Image][성공 테스트] Ls Image - 특정 이미지를 조회하는 경우 중 요청에 이미지태그까지 있는경우")
        void 특정이미지를_조회하는경우_요청에태그까지_있는경우_OauthServiceId에_해당하는_Image를_조회한다() {
            // given
            String oauthServiceId = "testOauthServiceId";
            String imageName = "tomcat:latest";

            // when
            List<ImageElements> actual = imageEntityRepository
                    .findAllByOauthServiceId(oauthServiceId, imageName);

            // then
            assertThat(compareEntityAndDto(testImage, actual.get(0)));
        }


        @Tag("domain")
        @Test
        @DisplayName("[Image][성공 테스트] Ls Image - 특정 이미지를 조회하는 경우 중 요청에 이미지 이름만 있는경우")
        void 특정이미지를_조회하는경우_요청에태그가_없는경우_OauthServiceId에_해당하는_Image를_조회한다() {
            // given
            String oauthServiceId = "testOauthServiceId";
            String imageName = "tomcat";

            // when
            List<ImageElements> actual = imageEntityRepository
                    .findAllByOauthServiceId(oauthServiceId, imageName);

            // then
            assertThat(compareEntityAndDto(testImage, actual.get(0)));
        }

        @Tag("domain")
        @Test
        @DisplayName("[Image][성공 테스트] Ls Image - 특정 이미지를 조회하지 않는경우")
        void 특정이미지를_조회하지않는경우_OauthServiceId에_해당하는_Image를_모두_조회한다() {
            // given
            String oauthServiceId = "testOauthServiceId";
            String imageName = null;

            // when && then
            Assertions.assertDoesNotThrow(
                    () -> imageEntityRepository.findAllByOauthServiceId(oauthServiceId, imageName)
            );
        }

        @Tag("domain")
        @Test
        @DisplayName("[Image][실패 테스트] Ls Image - OAuthServiceId이 Null인 경우 조회에 실패한다.")
        void OauthServiceId이_Null인_경우_조회에_실패한다() {
            // given
            String oauthServiceId = null;
            String imageName = "tomcat:latest";

            // when && then
            Assertions.assertThrows(
                    InvalidDataAccessApiUsageException.class,
                    () -> imageEntityRepository.findAllByOauthServiceId(oauthServiceId, imageName)
            );
        }
    }

    @Nested
    @DisplayName("[Image][성공/실패 테스트] Docker Image의 자세한 정보를 조회한다.")
    class InspectDockerImage {

        @BeforeEach
        void beforeEach() {
            testAccount = AccountEntity.builder()
                    .nickname("테스트용 계정1")
                    .oauthServiceId("testOauthServiceId")
                    .thirdPartyType(ThirdPartyEnum.KAKAO)
                    .isActive(true)
                    .build();

            testAccount.setDateTimeForTest(LocalDateTime.now(), LocalDateTime.now());
            accountRepository.save(testAccount);

            testImage = ImageEntity.builder()
                    .name("tomcat")
                    .tag("latest")
                    .os("pulledImageOs")
                    .architecture("pulledImageArchitecture")
                    .size("pulledImageSize")
                    .account(testAccount)
                    .build();

            testImage.setDateTimeForTest(LocalDateTime.now(), LocalDateTime.now());
            imageEntityRepository.save(testImage);
        }

        private boolean compareEntityAndDto(ImageEntity imageEntity, ImageDetailElements imageDto) {
            if (false == imageEntity.getName().equals(imageDto.getName())
                    || false == imageEntity.getTag().equals(imageDto.getTag())
                    || false == imageEntity.getCreateDate().equals(imageDto.getCreateDate())
                    || false == imageEntity.getUpdateDate().equals(imageDto.getUpdateDate())
                    || false == imageEntity.getSize().equals(imageDto.getSize())
                    || false == imageEntity.getOs().equals(imageDto.getOs())
                    || false == imageEntity.getArchitecture().equals(imageDto.getArchitecture())) {
                return false;
            }
            return true;
        }

        @Tag("domain")
        @Test
        @DisplayName("[Image][성공 테스트] Inspect Image - 특정 이미지를 조회하는 경우 중 요청에 이미지태그까지 있는경우")
        void 특정이미지를_조회하는경우_요청에태그까지_있는경우_Image_이름과_태그에맞는_이미지를_조회한다() {
            // given
            String oauthServiceId = "testOauthServiceId";
            String imageName = "tomcat:latest";

            // when
            ImageDetailElements actual = imageEntityRepository
                    .inspectImage(oauthServiceId, imageName);

            // then
            assertThat(compareEntityAndDto(testImage, actual));
        }


        @Tag("domain")
        @Test
        @DisplayName("[Image][성공 테스트] Inspect Image - 특정 이미지를 조회하는 경우 중 요청에 이미지 이름만 있는경우")
        void 특정이미지를_조회하는경우_요청에태그가_없는경우_Image_이름에맞는_이미지를_조회한다() {
            // given
            String oauthServiceId = "testOauthServiceId";
            String imageName = "tomcat";

            // when
            ImageDetailElements actual = imageEntityRepository
                    .inspectImage(oauthServiceId, imageName);

            // then
            assertThat(compareEntityAndDto(testImage, actual));
        }

        @Tag("domain")
        @Test
        @DisplayName("[Image][실패 테스트] Inspect Image - ImageName이 Null인 경우")
        void 이미지_Name이_Null인_경우_이미지_조회에_실패한다() {
            // given
            String oauthServiceId = "testOauthServiceId";
            String imageName = null;

            // when && then
            Assertions.assertThrows(
                    InvalidDataAccessApiUsageException.class,
                    () -> imageEntityRepository.inspectImage(oauthServiceId, imageName)
            );
        }

        @Tag("domain")
        @Test
        @DisplayName("[Image][실패 테스트] Inspect Image - OAuthServiceId이 Null인 경우 조회에 실패한다.")
        void OauthServiceId이_Null인_경우_조회에_실패한다() {
            // given
            String oauthServiceId = null;
            String imageName = "tomcat:latest";

            // when && then
            Assertions.assertThrows(
                    InvalidDataAccessApiUsageException.class,
                    () -> imageEntityRepository.inspectImage(oauthServiceId, imageName)
            );
        }
    }

    @Nested
    @DisplayName("[Image][성공/실패 테스트] Docker Image를 삭제한다.")
    class RmDockerImage {

        @BeforeEach
        void beforeEach() {
            testAccount = AccountEntity.builder()
                    .nickname("테스트용 계정1")
                    .oauthServiceId("testOauthServiceId")
                    .thirdPartyType(ThirdPartyEnum.KAKAO)
                    .isActive(true)
                    .build();

            testAccount.setDateTimeForTest(LocalDateTime.now(), LocalDateTime.now());
            accountRepository.save(testAccount);

            testImage = ImageEntity.builder()
                    .name("tomcat")
                    .tag("latest")
                    .os("pulledImageOs")
                    .architecture("pulledImageArchitecture")
                    .size("pulledImageSize")
                    .account(testAccount)
                    .build();

            testImage.setDateTimeForTest(LocalDateTime.now(), LocalDateTime.now());
            imageEntityRepository.save(testImage);
        }

        @Tag("domain")
        @Test
        @DisplayName("[Image][성공 테스트] Rm Image - 특정 이미지를 삭제하는 경우 중 요청에 이미지태그까지 있는경우")
        void 특정이미지를_삭제하는경우_요청에태그까지_있는경우_Image_이름과_태그에맞는_이미지를_삭제한다() {
            // given
            String oauthServiceId = "testOauthServiceId";
            String imageName = "tomcat:latest";

            // when
            boolean actual = imageEntityRepository
                    .rmImage(oauthServiceId, imageName);

            // then
            Assertions.assertTrue(actual);
        }


        @Tag("domain")
        @Test
        @DisplayName("[Image][성공 테스트] Rm Image - 특정 이미지를 삭제하는 경우 중 요청에 이미지 이름만 있는경우")
        void 특정이미지를_삭제하는경우_요청에태그가_없는경우_Image_이름에맞는_이미지에_latest_태그를_삭제한다() {
            // given
            String oauthServiceId = "testOauthServiceId";
            String imageName = "tomcat";

            // when
            boolean actual = imageEntityRepository
                    .rmImage(oauthServiceId, imageName);

            // then
            Assertions.assertTrue(actual);
        }

        @Tag("domain")
        @Test
        @DisplayName("[Image][실패 테스트] Rm Image - ImageName이 Null인 경우")
        void 이미지_Name이_Null인_경우_이미지_삭제에_실패한다() {
            // given
            String oauthServiceId = "testOauthServiceId";
            String imageName = null;

            // when && then
            Assertions.assertThrows(
                    InvalidDataAccessApiUsageException.class,
                    () -> imageEntityRepository.rmImage(oauthServiceId, imageName)
            );
        }

        @Tag("domain")
        @Test
        @DisplayName("[Image][실패 테스트] Rm Image - OAuthServiceId이 Null인 경우 삭제에 실패한다.")
        void OauthServiceId이_Null인_경우_삭제에_실패한다() {
            // given
            String oauthServiceId = null;
            String imageName = "tomcat:latest";

            // when && then
            Assertions.assertThrows(
                    InvalidDataAccessApiUsageException.class,
                    () -> imageEntityRepository.rmImage(oauthServiceId, imageName)
            );
        }
    }
}
