package org.chs.restdockerapis.image.application;

import lombok.RequiredArgsConstructor;
import org.chs.domain.account.AccountRepository;
import org.chs.domain.account.entity.AccountEntity;
import org.chs.domain.dockerhub.DockerHubEntityRepository;
import org.chs.domain.dockerhub.entity.DockerHubEntity;
import org.chs.domain.image.ImageEntityRepository;
import org.chs.domain.image.dto.ImageDetailElements;
import org.chs.domain.image.dto.ImageElements;
import org.chs.domain.image.entity.ImageEntity;
import org.chs.restdockerapis.common.argument_resolver.dto.GetRequesterDto;
import org.chs.restdockerapis.common.exception.CustomBadRequestException;
import org.chs.restdockerapis.common.exception.ErrorCode;
import org.chs.restdockerapis.common.util.ListUtils;
import org.chs.restdockerapis.image.presentation.dto.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Transactional
@RequiredArgsConstructor
public class ImageService {

    private final ListUtils listUtils;

    private final AccountRepository accountRepository;
    private final ImageEntityRepository dockerImageRepository;
    private final DockerHubEntityRepository dockerHubEntityRepository;

    /**
     * 예상 명령어 : docker image ls, docker images, docker image ls ${이미지 이름}, docker images ${이미지 이름}
     *
     * @param requesterInfo 사용자 기본 정보 (IP, OAuthServiceId, AccessToken, RefreshToken)
     * @param request ArgCommands : 명령어의 추가 요구사항 List(인자 값)
     * @return Image 조회의 결과 List
     */
    @Transactional(readOnly = true)
    public LsImageResponseDto lsImage(GetRequesterDto requesterInfo, DockerCommandRequestDto request) {
        String imageName = existArgOnlyOneImageName(request.getArgCommands());
        String oauthServiceId = requesterInfo.id();

        List<ImageElements> imageElementList = dockerImageRepository.findAllByOauthServiceId(
                oauthServiceId,
                imageName
        );

        return LsImageResponseDto.builder()
                .lsImageList(imageElementList)
                .build();
    }

    /**
     * 예상 명령어 : docker image pull ${이미지 이름}, docker pull ${이미지 이름}
     * 해당 요청은 서버에서 정해준 이미지만을 Pull 받을 수 있음
     *
     * @param requesterInfo 사용자 기본 정보 (IP, OAuthServiceId, AccessToken, RefreshToken)
     * @param request ArgCommands : 명령어의 추가 요구사항 List(인자 값)
     * @return Image Pull의 결과 List
     */
    public PullImageResponseDto pullImage(GetRequesterDto requesterInfo, DockerCommandRequestDto request) {
        String imageName = existArgOnlyOneImageName(request.getArgCommands());
        DockerHubEntity pulledImage = dockerHubEntityRepository.selectDockerImage(imageName);
        if (null == pulledImage) {
            throw new CustomBadRequestException(ErrorCode.NOT_EXIST_IMAGE_IN_DOCKERHUB);
        }

        AccountEntity account = accountRepository
                .findByOauthServiceIdEqualsAndThirdPartyTypeEquals(requesterInfo.id(), requesterInfo.thirdPartyType())
                .orElseThrow(() -> new IllegalArgumentException("토큰으로 온 사용자의 정보가 없습니다."));

        ImageEntity savedImage = dockerImageRepository.save(
                ImageEntity.builder()
                        .name(pulledImage.getName())
                        .os(pulledImage.getOs())
                        .architecture(pulledImage.getArchitecture())
                        .tag(pulledImage.getTag())
                        .size(pulledImage.getSize())
                        .account(account)
                        .build()
        );

        return PullImageResponseDto.builder()
                .pullImageFullName(savedImage.getFullName())
                .build();
    }

    /**
     * 예상 명령어 : docker image inspect ${이미지 이름}
     *
     * @param requesterInfo 사용자 기본 정보 (IP, OAuthServiceId, AccessToken, RefreshToken)
     * @param request ArgCommands : 명령어의 추가 요구사항 List(인자 값)
     * @return Image 의 자세한 정보
     */
    @Transactional(readOnly = true)
    public InspectImageResponseDto inspectImage(GetRequesterDto requesterInfo, DockerCommandRequestDto request) {
        String imageName = existArgOnlyOneImageName(request.getArgCommands());
        if (null == imageName) {
            throw new CustomBadRequestException(ErrorCode.ARGUMENT_COMMAND_NOT_VALID_EXCEPTION);
        }
        String oauthServiceId = requesterInfo.id();

        ImageDetailElements inspectedImageDetailElement = dockerImageRepository.inspectImage(oauthServiceId, imageName);

        return InspectImageResponseDto.builder()
                .inspectImage(inspectedImageDetailElement)
                .build();
    }

    /**
     * 예상 명령어 : docker image rm ${이미지 이름}, docker rmi ${이미지 이름}
     *
     * @param requesterInfo 사용자 기본 정보 (IP, OAuthServiceId, AccessToken, RefreshToken)
     * @param request ArgCommands : 명령어의 추가 요구사항 List(인자 값)
     * @return Image 삭제 성공유무
     */
    public RmImageResponseDto rmImage(GetRequesterDto requesterInfo, DockerCommandRequestDto request) {
        String imageName = existArgOnlyOneImageName(request.getArgCommands());
        if (null == imageName) {
            throw new CustomBadRequestException(ErrorCode.ARGUMENT_COMMAND_NOT_VALID_EXCEPTION);
        }
        String oauthServiceId = requesterInfo.id();

        boolean deleteImageResult = dockerImageRepository.rmImage(oauthServiceId, imageName);

        return RmImageResponseDto.builder()
                .imageDeleteResult(deleteImageResult)
                .build();
    }

    private String existArgOnlyOneImageName(List<String> argCommands) {
        if (listUtils.existAndNotSizeOne(argCommands)) {
            throw new CustomBadRequestException(ErrorCode.ARGUMENT_COMMAND_NOT_VALID_EXCEPTION);
        }

        return listUtils.isBlank(argCommands) ? null : argCommands.get(0);
    }
}
