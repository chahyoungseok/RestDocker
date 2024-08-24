package org.chs.restdockerapis.image.application;

import org.chs.domain.account.AccountRepository;
import org.chs.domain.account.entity.AccountEntity;
import org.chs.domain.common.enumerate.ThirdPartyEnum;
import org.chs.domain.dockerhub.DockerHubEntityRepository;
import org.chs.domain.dockerhub.entity.DockerHubEntity;
import org.chs.domain.image.ImageEntityRepository;
import org.chs.domain.image.dto.ImageDetailElements;
import org.chs.domain.image.dto.ImageElements;
import org.chs.domain.image.entity.ImageEntity;
import org.chs.restdockerapis.common.argument_resolver.dto.GetRequesterDto;
import org.chs.restdockerapis.common.exception.CustomBadRequestException;
import org.chs.restdockerapis.common.util.ListUtils;
import org.chs.restdockerapis.image.presentation.dto.*;
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

import static org.mockito.ArgumentMatchers.any;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class ImageServiceTest {

    @InjectMocks
    private ImageService imageService;

    @Mock
    private ListUtils listUtils;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private ImageEntityRepository dockerImageRepository;

    @Mock
    private DockerHubEntityRepository dockerHubEntityRepository;

    private GetRequesterDto testRequestInfo = null;
    private GetRequesterDto testInValidRequestInfo = null;

    private DockerCommandRequestDto testManyArgRequest = null;
    private DockerCommandRequestDto testRequest = null;
    private DockerCommandRequestDto testEmptyRequest = null;

    private ImageServiceTest() {
        // given - data
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

        testManyArgRequest = DockerCommandRequestDto.builder()
                .argCommands(List.of("tomcat", "mysql"))
                .build();

        testRequest = DockerCommandRequestDto.builder()
                .argCommands(List.of("tomcat"))
                .build();

        testEmptyRequest = DockerCommandRequestDto.builder().build();
    }

    @Nested
    @DisplayName("[Image][시나리오 테스트] 이미지 조회(LS)를 테스트한다.")
    class LSImage {

        private ImageElements testImageElements = null;
        private LsImageResponseDto testResponse = null;

        protected LSImage() {
            // given - data
            testImageElements = ImageElements.builder()
                    .createDate(LocalDateTime.parse("2024-01-01T00:00:00"))
                    .updateDate(LocalDateTime.parse("2024-01-01T00:00:00"))
                    .name("tomcat")
                    .tag("latest")
                    .size("490702439")
                    .build();

            testResponse = LsImageResponseDto.builder()
                    .lsImageList(List.of(testImageElements))
                    .build();
        }

        @Tag("business")
        @Test
        @DisplayName("[Image][Business] 인자값이 없을 때, 이미지 조회를 정상적으로 성공한다.")
        void 인자값이_없을_때_이미지_조회를_정상적으로_성공한다() {
            // given - mocking
            BDDMockito.given(dockerImageRepository.findAllByOauthServiceId(any(), any()))
                    .willReturn(List.of(testImageElements));

            BDDMockito.given(listUtils.existAndNotSizeOne(any()))
                    .willReturn(false);

            BDDMockito.given(listUtils.isBlank(any()))
                    .willReturn(true);

            // when
            LsImageResponseDto actual = imageService.lsImage(testRequestInfo, testEmptyRequest);

            // then
            Assertions.assertEquals(testResponse.getLsImageList(), actual.getLsImageList());
        }

        @Tag("business")
        @Test
        @DisplayName("[Image][Business] 인자값이 있을 때, 이미지 조회를 정상적으로 성공한다.")
        void 인자값이_있을_때_이미지_조회를_정상적으로_성공한다() {
            // given - mocking
            BDDMockito.given(dockerImageRepository.findAllByOauthServiceId(any(), any()))
                    .willReturn(List.of(testImageElements));

            BDDMockito.given(listUtils.existAndNotSizeOne(any()))
                    .willReturn(false);

            BDDMockito.given(listUtils.isBlank(any()))
                    .willReturn(false);

            // when
            LsImageResponseDto actual = imageService.lsImage(testRequestInfo, testRequest);

            // then
            Assertions.assertEquals(testResponse.getLsImageList(), actual.getLsImageList());
        }

        @Tag("business")
        @Test
        @DisplayName("[Image][Business] 인자값이 있을 때, 사용자가 원하는 이미지가 DB에 존재하지 않는경우 실패한다")
        void 인자값이_있을_때_사용자가_원하는_이미지가_DB에_존재하지않는경우_실패한다() {
            // given - mocking
            BDDMockito.given(dockerImageRepository.findAllByOauthServiceId(any(), any()))
                    .willReturn(null);

            BDDMockito.given(listUtils.existAndNotSizeOne(any()))
                    .willReturn(false);

            BDDMockito.given(listUtils.isBlank(any()))
                    .willReturn(true);

            // when
            LsImageResponseDto actual = imageService.lsImage(testRequestInfo, testRequest);

            // then
            Assertions.assertNull(actual.getLsImageList());
        }

        @Tag("business")
        @Test
        @DisplayName("[Image][Business] 인자값이 있을 때, 인자값이 1개만 들어있지 않은경우 실패한다.")
        void 인자값이_있을_때_인자값이_1개만_들어있지_않은경우_실패한다() {
            // given - mocking
            BDDMockito.given(listUtils.existAndNotSizeOne(any()))
                    .willReturn(true);

            // when && then
            Assertions.assertThrows(
                    CustomBadRequestException.class,
                    () -> imageService.lsImage(testRequestInfo, testManyArgRequest)
            );
        }

        @Tag("business")
        @Test
        @DisplayName("[Image][Business] 토큰으로 온 사용자 정보가 유효하지 않을 때 실패한다")
        void 토큰으로_온_사용자_정보가_유효하지_않을때_실패한다() {
            // given - mocking
            BDDMockito.given(dockerImageRepository.findAllByOauthServiceId(any(), any()))
                    .willThrow(new IllegalArgumentException("OAuthServiceId를 가진 계정이 존재하지 않습니다."));

            // when && then
            Assertions.assertThrows(
                    IllegalArgumentException.class,
                    () -> imageService.lsImage(testInValidRequestInfo, testRequest)
            );
        }
    }

    @Nested
    @DisplayName("[Image][시나리오 테스트] 이미지 가져오기(Pull)를 테스트한다.")
    class PullImage {

        private String pullImageFullName = null;
        private PullImageResponseDto testResponse = null;

        private DockerHubEntity pulledImage = null;
        private AccountEntity account = null;
        private ImageEntity savedImage = null;

        protected PullImage() {
            // given - data
            pullImageFullName = "tomcat:latest";

            testResponse = PullImageResponseDto.builder()
                    .pullImageFullName(pullImageFullName)
                    .build();

            pulledImage = DockerHubEntity.builder()
                    .name("tomcat")
                    .tag("latest")
                    .os("pulledImageOs")
                    .architecture("pulledImageArchitecture")
                    .size("pulledImageSize")
                    .build();

            account = AccountEntity.builder()
                    .thirdPartyAccessToken("testThirdPartyAccessToken")
                    .thirdPartyRefreshToken("testThirdPartyRefreshToken")
                    .nickname("testNickname")
                    .oauthServiceId("testOAuthServiceId")
                    .isActive(true)
                    .build();

            savedImage = ImageEntity.builder()
                    .name(pulledImage.getName())
                    .os(pulledImage.getOs())
                    .architecture(pulledImage.getArchitecture())
                    .tag(pulledImage.getTag())
                    .size(pulledImage.getSize())
                    .account(account)
                    .build();
        }

        @Tag("business")
        @Test
        @DisplayName("[Image][Business] 인자값이 있을 때, 이미지 가져오기를 정상적으로 성공한다.")
        void 인자값이_있을_때_이미지_가져오기를_정상적으로_성공한다() {
            // given - mocking
            BDDMockito.given(listUtils.existAndNotSizeOne(any()))
                    .willReturn(false);

            BDDMockito.given(listUtils.isBlank(any()))
                    .willReturn(false);

            BDDMockito.given(dockerHubEntityRepository.selectDockerImage(any()))
                    .willReturn(pulledImage);

            BDDMockito.given(accountRepository.findByOauthServiceIdEqualsAndThirdPartyTypeEquals(any(), any()))
                    .willReturn(Optional.of(account));

            BDDMockito.given(dockerImageRepository.save(any()))
                    .willReturn(savedImage);

            // when
            PullImageResponseDto actual = imageService.pullImage(testRequestInfo, testRequest);

            // then
            Assertions.assertEquals(testResponse.getPullImageFullName(), actual.getPullImageFullName());
        }

        @Tag("business")
        @Test
        @DisplayName("[Image][Business] 인자값이 없을 때, 이미지 가져오기를 실패한다.")
        void 인자값이_없을_때_이미지_가져오기를_실패한다() {
            // given - mocking
            BDDMockito.given(listUtils.existAndNotSizeOne(any()))
                    .willReturn(false);

            BDDMockito.given(listUtils.isBlank(any()))
                    .willReturn(true);

            // when && then
            Assertions.assertThrows(
                    CustomBadRequestException.class,
                    () -> imageService.pullImage(testRequestInfo, testEmptyRequest)
            );
        }

        @Tag("business")
        @Test
        @DisplayName("[Image][Business] 토큰으로 온 사용자 정보가 유효하지 않을 때 실패한다")
        void 토큰으로_온_사용자_정보가_유효하지_않을때_실패한다() {
            // given - mocking
            BDDMockito.given(listUtils.existAndNotSizeOne(any()))
                    .willReturn(false);

            BDDMockito.given(listUtils.isBlank(any()))
                    .willReturn(false);

            BDDMockito.given(dockerHubEntityRepository.selectDockerImage(any()))
                    .willReturn(pulledImage);

            BDDMockito.given(accountRepository.findByOauthServiceIdEqualsAndThirdPartyTypeEquals(any(), any()))
                    .willThrow(new IllegalArgumentException("토큰으로 온 사용자의 정보가 없습니다."));

            // when && then
            Assertions.assertThrows(
                    IllegalArgumentException.class,
                    () -> imageService.pullImage(testInValidRequestInfo, testRequest)
            );
        }

        @Tag("business")
        @Test
        @DisplayName("[Image][Business] 인자값이 있을 때, 인자값이 1개만 들어있지 않은경우 실패한다.")
        void 인자값이_있을_때_인자값이_1개만_들어있지_않은경우_실패한다() {
            // given - mocking
            BDDMockito.given(listUtils.existAndNotSizeOne(any()))
                    .willReturn(true);

            // when && then
            Assertions.assertThrows(
                    CustomBadRequestException.class,
                    () -> imageService.pullImage(testRequestInfo, testManyArgRequest)
            );
        }
    }

    @Nested
    @DisplayName("[Image][시나리오 테스트] 이미지 정보 자세히 조회(Inspect)를 테스트한다.")
    class InspectImage {

        private ImageDetailElements inspectImage = null;
        private InspectImageResponseDto testResponse = null;

        protected InspectImage() {
            // given - data
            inspectImage = ImageDetailElements.builder()
                    .createDate(LocalDateTime.parse("2024-01-01T00:00:00"))
                    .updateDate(LocalDateTime.parse("2024-01-01T00:00:00"))
                    .name("tomcat")
                    .tag("latest")
                    .size("490702439")
                    .architecture("arm64")
                    .os("linux")
                    .build();

            testResponse = InspectImageResponseDto.builder()
                    .inspectImage(inspectImage)
                    .build();
        }

        @Tag("business")
        @Test
        @DisplayName("[Image][Business] 인자값이 있을 때, 자세한 이미지 정보조회를 성공한다.")
        void 인자값이_있을_때_자세한_이미지_정보조회를_성공한다() {
            // given - mocking
            BDDMockito.given(listUtils.existAndNotSizeOne(any()))
                    .willReturn(false);

            BDDMockito.given(listUtils.isBlank(any()))
                    .willReturn(false);

            BDDMockito.given(dockerImageRepository.inspectImage(any(), any()))
                    .willReturn(inspectImage);

            // when
            InspectImageResponseDto actual = imageService.inspectImage(testRequestInfo, testRequest);

            // then
            Assertions.assertEquals(testResponse.getInspectImage(), actual.getInspectImage());
        }

        @Tag("business")
        @Test
        @DisplayName("[Image][Business] 인자값이 없을 때, 이미지 가져오기를 실패한다.")
        void 인자값이_없을_때_이미지_가져오기를_실패한다() {
            // given - mocking
            BDDMockito.given(listUtils.existAndNotSizeOne(any()))
                    .willReturn(false);

            BDDMockito.given(listUtils.isBlank(any()))
                    .willReturn(true);

            // when && then
            Assertions.assertThrows(
                    CustomBadRequestException.class,
                    () -> imageService.inspectImage(testRequestInfo, testEmptyRequest)
            );
        }

        @Tag("business")
        @Test
        @DisplayName("[Image][Business] 인자값이 있을 때, 인자값이 1개만 들어있지 않은경우 실패한다.")
        void 인자값이_있을_때_인자값이_1개만_들어있지_않은경우_실패한다() {
            // given - mocking
            BDDMockito.given(listUtils.existAndNotSizeOne(any()))
                    .willReturn(true);

            // when && then
            Assertions.assertThrows(
                    CustomBadRequestException.class,
                    () -> imageService.inspectImage(testRequestInfo, testManyArgRequest)
            );
        }

        @Tag("business")
        @Test
        @DisplayName("[Image][Business] 토큰으로 온 사용자 정보가 유효하지 않을때 실패한다.")
        void 토큰으로_온_사용자_정보가_유효하지_않을때_실패한다() {
            // given - mocking
            BDDMockito.given(listUtils.existAndNotSizeOne(any()))
                    .willReturn(false);

            BDDMockito.given(listUtils.isBlank(any()))
                    .willReturn(false);

            BDDMockito.given(dockerImageRepository.inspectImage(any(), any()))
                    .willThrow(new IllegalArgumentException("OAuthServiceId를 가진 계정이 존재하지 않습니다."));

            // when && then
            Assertions.assertThrows(
                    IllegalArgumentException.class,
                    () -> imageService.inspectImage(testInValidRequestInfo, testRequest)
            );
        }
    }

    @Nested
    @DisplayName("[Image][시나리오 테스트] 이미지 삭제(Rm)를 테스트한다.")
    class RmImage {

        private RmImageResponseDto testSuccessResponse = null;
        private RmImageResponseDto testFailResponse = null;

        protected RmImage() {
            // given - data
            testSuccessResponse = RmImageResponseDto.builder()
                    .imageDeleteResult(true)
                    .build();

            testFailResponse = RmImageResponseDto.builder()
                    .imageDeleteResult(false)
                    .build();
        }

        @Tag("business")
        @Test
        @DisplayName("[Image][Business] 인자값이 있을 때, 이미지 삭제를 성공한다.")
        void 인자값이_있을_때_이미지_삭제를_성공한다() {
            // given - mocking
            BDDMockito.given(listUtils.existAndNotSizeOne(any()))
                    .willReturn(false);

            BDDMockito.given(listUtils.isBlank(any()))
                    .willReturn(false);

            BDDMockito.given(dockerImageRepository.rmImage(any(), any()))
                    .willReturn(true);

            // when
            RmImageResponseDto actual = imageService.rmImage(testRequestInfo, testRequest);

            // then
            Assertions.assertEquals(testSuccessResponse.isImageDeleteResult(), actual.isImageDeleteResult());
        }

        @Tag("business")
        @Test
        @DisplayName("[Image][Business] 인자값이 있을 때, 이미지 삭제를 실패한다.")
        void 인자값이_있을_때_이미지_삭제를_실패한다() {
            // given - mocking
            BDDMockito.given(listUtils.existAndNotSizeOne(any()))
                    .willReturn(false);

            BDDMockito.given(listUtils.isBlank(any()))
                    .willReturn(false);

            BDDMockito.given(dockerImageRepository.rmImage(any(), any()))
                    .willReturn(false);

            // when
            RmImageResponseDto actual = imageService.rmImage(testRequestInfo, testRequest);

            // then
            Assertions.assertEquals(testFailResponse.isImageDeleteResult(), actual.isImageDeleteResult());
        }

        @Tag("business")
        @Test
        @DisplayName("[Image][Business] 인자값이 없을 때, 이미지 삭제를 실패한다.")
        void 인자값이_없을_때_이미지_삭제를_실패한다() {
            // given - mocking
            BDDMockito.given(listUtils.existAndNotSizeOne(any()))
                    .willReturn(false);

            BDDMockito.given(listUtils.isBlank(any()))
                    .willReturn(true);

            // when && then
            Assertions.assertThrows(
                    CustomBadRequestException.class,
                    () -> imageService.rmImage(testRequestInfo, testEmptyRequest)
            );
        }

        @Tag("business")
        @Test
        @DisplayName("[Image][Business] 인자값이 있을 때, 인자값이 1개만 들어있지 않은경우 실패한다.")
        void 인자값이_있을_때_인자값이_1개만_들어있지_않은경우_실패한다() {
            // given - mocking
            BDDMockito.given(listUtils.existAndNotSizeOne(any()))
                    .willReturn(true);

            // when && then
            Assertions.assertThrows(
                    CustomBadRequestException.class,
                    () -> imageService.rmImage(testRequestInfo, testManyArgRequest)
            );
        }

        @Tag("business")
        @Test
        @DisplayName("[Image][Business] 토큰으로 온 사용자 정보가 유효하지 않을때 실패한다.")
        void 토큰으로_온_사용자_정보가_유효하지_않을때_실패한다() {
            // given - mocking
            BDDMockito.given(listUtils.existAndNotSizeOne(any()))
                    .willReturn(false);

            BDDMockito.given(listUtils.isBlank(any()))
                    .willReturn(false);

            BDDMockito.given(dockerImageRepository.rmImage(any(), any()))
                    .willThrow(new IllegalArgumentException("OAuthServiceId를 가진 계정이 존재하지 않습니다."));

            // when && then
            Assertions.assertThrows(
                    IllegalArgumentException.class,
                    () -> imageService.rmImage(testInValidRequestInfo, testRequest)
            );
        }
    }
}
