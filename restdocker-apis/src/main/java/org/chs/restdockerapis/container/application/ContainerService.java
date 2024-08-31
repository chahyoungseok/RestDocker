package org.chs.restdockerapis.container.application;

import lombok.RequiredArgsConstructor;
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
import org.chs.domain.network.entity.NetworkContainerMappingEntity;
import org.chs.domain.network.entity.NetworkEntity;
import org.chs.restdockerapis.common.argument_resolver.dto.GetRequesterDto;
import org.chs.restdockerapis.common.exception.CustomBadRequestException;
import org.chs.restdockerapis.common.exception.ErrorCode;
import org.chs.restdockerapis.container.presentation.dto.*;
import org.chs.restdockerapis.image.presentation.dto.DockerCommandRequestDto;
import org.chs.restdockerapis.network.util.AddressUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ContainerService {

    private final ImageEntityRepository imageEntityRepository;
    private final NetworkEntityRepository networkEntityRepository;
    private final ContainerEntityRepository containerEntityRepository;
    private final NetworkContainerMappingEntityRepository networkContainerMappingEntityRepository;

    private final AddressUtils addressUtils;

    /**
     * 예상 명령어 : docker ps, docker container ps, docker ls, docker container ls
     * 예상 인자 :
     *          -l (가장 마지막에 생성된 컨테이너)
     *          -a (모든 상태의 컨테이너)
     *
     * @param requesterInfo 사용자 기본 정보 (IP, OAuthServiceId, AccessToken, RefreshToken, ThirdPartyEnum)
     * @param request 명령어의 추가 요구사항 List(인자 값)
     * @return 요청을 보낸 사용자에 해당하고, 조건에 부합하는 모든 컨테이너
     */
    @Transactional(readOnly = true)
    public LsContainerResponseDto lsContainer(GetRequesterDto requesterInfo, DockerCommandRequestDto request) {
        boolean allStatus = false;
        boolean lastCreate = false;

        for (String arg : request.argCommands()) {
            switch (arg) {
                case "-a" : {
                    if (allStatus)
                        throw new CustomBadRequestException(ErrorCode.ARGUMENT_COMMAND_NOT_VALID_EXCEPTION);

                    allStatus = true;
                    break;
                }
                case "-l" : {
                    if (lastCreate)
                        throw new CustomBadRequestException(ErrorCode.ARGUMENT_COMMAND_NOT_VALID_EXCEPTION);

                    lastCreate = true;
                    break;
                }
                default:
                    throw new CustomBadRequestException(ErrorCode.ARGUMENT_COMMAND_NOT_VALID_EXCEPTION);
            }
        }

        List<ContainerElements> containerElementsList = containerEntityRepository.lsContainer(requesterInfo.id());

        if (false == allStatus) {
            containerElementsList = filterContainerRunning(containerElementsList);
        }
        if (true == lastCreate) {
            containerElementsList = filterLastCreateContainer(containerElementsList);
        }

        return LsContainerResponseDto.builder()
                .containerElementsList(containerElementsList)
                .build();
    }

    private List<ContainerElements> filterContainerRunning(List<ContainerElements> containerElementsList) {
        return containerElementsList.stream().filter(element -> {
            if (element.getStatus().equals(ContainerStatusEnum.Running)) {
                return true;
            }
            return false;
        }).toList();
    }

    private List<ContainerElements> filterLastCreateContainer(List<ContainerElements> containerElementsList) {
        return containerElementsList.stream()
                .max(Comparator.comparing(ContainerElements::getCreateDate))
                .stream().toList();
    }

    /**
     * 예상 명령어 : docker inspect ${컨테이너 이름}
     * 예상 인자 : 없음
     *
     * @param requesterInfo 사용자 기본 정보 (IP, OAuthServiceId, AccessToken, RefreshToken, ThirdPartyEnum)
     * @param request 명령어의 추가 요구사항 List(인자 값)
     * @return 요청을 보낸 사용자에 해당하고, 요청 이름에 맞는 특정 컨테이너의 자세한 정보
     */
    public InspectContainerResponseDto inspectContainer(GetRequesterDto requesterInfo, DockerCommandRequestDto request) {
        String containerName = getContainerNameForOneArgCommand(request.argCommands());

        ContainerDetailElements containerDetailElements = containerEntityRepository.inspectContainer(requesterInfo.id(), containerName);

        return InspectContainerResponseDto.builder()
                .inspectContainerDetailElements(containerDetailElements)
                .build();
    }

    /**
     * 예상 명령어 : docker rename ${컨테이너 변경 전 이름} ${컨테이너 변경 후 이름}
     * 예상 인자 : 없음
     *
     * @param requesterInfo 사용자 기본 정보 (IP, OAuthServiceId, AccessToken, RefreshToken, ThirdPartyEnum)
     * @param request 명령어의 추가 요구사항 List(인자 값)
     * @return 컨테이너 이름 변경 성공유무
     */
    public RenameContainerResponseDto renameContainer(GetRequesterDto requesterInfo, DockerCommandRequestDto request) {
        if (2 != request.argCommands().size()) {
            throw new CustomBadRequestException(ErrorCode.ARGUMENT_COMMAND_NOT_VALID_EXCEPTION);
        }
        ContainerEntity container
                = containerEntityRepository.findContainerByOAuthServiceAndContainerName(requesterInfo.id(), request.argCommands().get(0));

        boolean renameResult = containerEntityRepository.renameContainer(
                container.getPk(), request.argCommands().get(1)
        );

        return RenameContainerResponseDto.builder()
                .renameResult(renameResult)
                .build();
    }

    /**
     * 예상 명령어 : docker container create ${컨테이너 이름}
     * 예상 인자 :
     *          --name ${도커 컨테이너 이름}
     *          --rm
     *          --net ${도커 네트워크 이름}
     *          --ip ${도커 네트워크 안에서 할당할 ip}
     *          -p ${외부 Port}:${내부 Port}
     *          ${이미지 이름}:${이미지 태그}
     *
     * 참고사항 :
     *          --net 이 없을경우 bridge 네트워크에 할당
     *          --net 이 존재하며 --ip가 없을 경우 ip를 자동할당
     *          --net 가 없는데 --ip 가 있다면 에러
     *
     * @param requesterInfo 사용자 기본 정보 (IP, OAuthServiceId, AccessToken, RefreshToken, ThirdPartyEnum)
     * @param request 명령어의 추가 요구사항 List(인자 값)
     * @return 생성한 컨테이너 이름
     */
    public CreateContainerResponseDto createContainer(GetRequesterDto requesterInfo, DockerCommandRequestDto request) {
        ContainerOptionDto castedContainerOptionDto = castCommandToDto(request.argCommands());
        ContainerOptionDto validedOptionDto = validContainerOption(castedContainerOptionDto, requesterInfo.id());

        ContainerEntity savedContainer = saveContainerForOptionDto(requesterInfo.id(), validedOptionDto);

        return CreateContainerResponseDto.builder()
                .containerName(savedContainer.getName())
                .build();
    }

    private ContainerOptionDto castCommandToDto(List<String> argCommands) {
        ContainerOptionDto containerOption = new ContainerOptionDto();

        for (String argCommand : argCommands) {

            if (argCommand.startsWith("-")) {
                containerOption.putArgCommand(argCommand.split(" "));
            }
            else {
                if (argCommand.contains(" ") || null != containerOption.getImageFullName()) {
                    throw new CustomBadRequestException(ErrorCode.ARGUMENT_COMMAND_NOT_VALID_EXCEPTION);
                }
                containerOption.setImageFullName(notExistImageTagAddLatest(argCommand));
            }
        }

        return containerOption;
    }

    private ContainerOptionDto validContainerOption(ContainerOptionDto containerOption, String oauthServiceId) {
        if (false == containerOption.validNotExistNetwork()) {
            throw new CustomBadRequestException(ErrorCode.ARGS_NEED_NETWORK);
        }

        if (null == containerOption.getNetworkName()) {
            containerOption.setBridgeNetwork();
        }

        List<ContainerValidElementsDto> validElementsList = containerEntityRepository.findValidElementsListByOAuthServiceId(oauthServiceId);

        validContainerName(
                validElementsList.stream().map(ContainerValidElementsDto::getContainerName).toList(),
                containerOption.getName()
        );

        validContainerIp(
                oauthServiceId,
                containerOption
        );

        validPortForwarding(
                validElementsList.stream().map(ContainerValidElementsDto::getOuterPort).toList(),
                containerOption
        );

        return containerOption;
    }

    private void validContainerName(List<String> containerNameList, String containerName) {
        if (containerNameList.contains(containerName)) {
            throw new CustomBadRequestException(ErrorCode.NOT_VALID_NAME);
        }
    }

    private void validContainerIp(String oauthServiceId, ContainerOptionDto containerOption) {
        if (null == containerOption.getContainerIp()) {
            allocationContainerIp(oauthServiceId, containerOption);
        }

        if (false == addressUtils.validAddressFormat(containerOption.getContainerIp())) {
            throw new CustomBadRequestException(ErrorCode.ARGUMENT_NOT_VALID_EXCEPTION);
        }

        if (false == validConflictContainerIp(oauthServiceId, containerOption.getNetworkName(), containerOption.getContainerIp())) {
            throw new CustomBadRequestException(ErrorCode.NOT_VALID_PRIVATEIP);
        }
    }

    private void validPortForwarding(List<String> outerPortList, ContainerOptionDto containerOption) {
        if (null != containerOption.getPortForward()) {
            if (false == addressUtils.validPortForwardingFormat(containerOption.getPortForward())) {
                throw new CustomBadRequestException(ErrorCode.NOT_VALID_PORTFOWARDING);
            }

            String[] ports = containerOption.getPortForward().split(":");
            if (outerPortList.contains(ports[0])) {
                throw new CustomBadRequestException(ErrorCode.NOT_VALID_PORTFOWARDING);
            }
        }
    }

    private boolean validConflictContainerIp(String oauthServiceId, String networkName, String containerIp) {
        List<String> containerIpList = networkContainerMappingEntityRepository.findPrivateIpByOAuthServiceIdAndNetworkName(oauthServiceId, networkName);

        return false == containerIpList.contains(containerIp);
    }

    private void allocationContainerIp(String oauthServiceId, ContainerOptionDto containerOption) {
        // net 이 존재하며 --ip가 없을 경우 ip를 자동할당
        NetworkDetailElements networkDetailElements = networkEntityRepository.inspectNetwork(oauthServiceId, containerOption.getNetworkName());
        networkDetailElements.setContainerInfo(
                containerEntityRepository.lsContainer(oauthServiceId)
        );

        String autoAllocationPrivateIp = addressUtils.automaticAllocationContainerIp(
                networkDetailElements.getSubnet(),
                null != networkDetailElements.getContainerInfo() ?
                        networkDetailElements.getContainerInfo().stream().map(ContainerElements::getPrivateIp).toList() :
                        null
        );

        if (null == autoAllocationPrivateIp) {
            throw new CustomBadRequestException(ErrorCode.NO_SPACE_DOCKER_HOST_SUBNET);
        }

        containerOption.setContainerIp(autoAllocationPrivateIp);
    }

    private String notExistImageTagAddLatest(String imageName) {
        if (false == imageName.contains(":")) {
            return imageName + ":latest";
        }

        return imageName;
    }

    private ContainerEntity saveContainerForOptionDto(String oauthServiceId, ContainerOptionDto containerOption) {
        String outerPort = null;
        String innerPort = null;

        if (null != containerOption.getPortForward()) {
            String[] ports = containerOption.getPortForward().split(":");
            if (2 != ports.length) {
                throw new CustomBadRequestException(ErrorCode.ARGUMENT_NOT_VALID_EXCEPTION);
            }
            outerPort = ports[0];
            innerPort = ports[1];
        }

        ImageEntity image = imageEntityRepository.findByOAuthServiceIdAndImageFullName(oauthServiceId, containerOption.getImageFullName());
        if (null == image) {
            throw new CustomBadRequestException(ErrorCode.NOT_EXIST_IMAGE_IN_HOST);
        }

        NetworkEntity network = networkEntityRepository.findByOAuthServiceIdAndNetworkName(oauthServiceId, containerOption.getNetworkName());
        if (null == network) {
            throw new CustomBadRequestException(ErrorCode.NOT_EXIST_NETWORK_IN_HOST);
        }

        ContainerEntity savedContainer = containerEntityRepository.save(
                ContainerEntity.builder()
                        .image(image)
                        .name(containerOption.getName())
                        .status(ContainerStatusEnum.Created)
                        .stopRm(containerOption.isRm())
                        .privateIp(containerOption.getContainerIp())
                        .outerPort(outerPort)
                        .innerPort(innerPort)
                        .build()
        );

        networkContainerMappingEntityRepository.save(
                NetworkContainerMappingEntity.builder()
                        .container(savedContainer)
                        .network(network)
                        .build()
        );

        return savedContainer;
    }

    /**
     * 예상 명령어 : docker rm ${컨테이너 이름}
     * 예상 인자 : 없음
     *
     * @param oauthServiceId 사용자 기본 정보
     * @param request 명령어의 추가 요구사항 List(인자 값)
     * @return 컨테이너 삭제 성공유무
     */
    public RmContainerResponseDto rmContainer(String oauthServiceId, DockerCommandRequestDto request) {
        String containerName = getContainerNameForOneArgCommand(request.argCommands());

        ContainerEntity container = containerEntityRepository.findContainerByOAuthServiceAndContainerName(oauthServiceId, containerName);
        if (null == container) {
            throw new CustomBadRequestException(ErrorCode.NOT_EXIST_CONTAINER);
        }

        boolean rmResult = containerEntityRepository.rmContainer(container.getPk());

        return RmContainerResponseDto.builder()
                .rmResult(rmResult)
                .build();
    }

    /**
     * 예상 명령어 : docker container run ${컨테이너 이름}
     * 예상 인자 :
     *          --name ${도커 컨테이너 이름}
     *          --rm
     *          --net ${도커 네트워크 이름}
     *          --ip ${도커 네트워크 안에서 할당할 ip}
     *          -p ${외부 Port}:${내부 Port}
     *          ${이미지 이름}:${이미지 태그}
     *
     * 참고사항 :
     *          docker run 은 docker create 와 docker start 가 합쳐진 명령어
     *
     * @param requesterInfo 사용자 기본 정보 (IP, OAuthServiceId, AccessToken, RefreshToken, ThirdPartyEnum)
     * @param request 명령어의 추가 요구사항 List(인자 값)
     * @return 컨테이너 생성 및 시작의 성공여부
     */
    public RunContainerResponseDto runContainer(GetRequesterDto requesterInfo, DockerCommandRequestDto request) {

        CreateContainerResponseDto createdContainer = this.createContainer(requesterInfo, request);
        DockerCommandRequestDto argCommands = DockerCommandRequestDto.builder()
                .argCommands(List.of(createdContainer.containerName()))
                .build();

        StartContainerResponseDto startContainerResponse = this.startContainer(requesterInfo.id(), argCommands);

        return RunContainerResponseDto.builder()
                .startResult(startContainerResponse.startResult())
                .build();
    }

    /**
     * 예상 명령어 : docker start ${컨테이너 이름}
     * 예상 인자 : 없음
     *
     * @param oauthServiceId 사용자 기본 정보
     * @param request 명령어의 추가 요구사항 List(인자 값)
     * @return 컨테이너 시작 성공유무
     */
    public StartContainerResponseDto startContainer(String oauthServiceId, DockerCommandRequestDto request) {
        String containerName = getContainerNameForOneArgCommand(request.argCommands());

        ContainerEntity targetContainer = containerEntityRepository.findContainerByOAuthServiceAndContainerName(oauthServiceId, containerName);
        if (ContainerStatusEnum.Running.equals(targetContainer.getStatus())) {
            throw new CustomBadRequestException(ErrorCode.ALREADY_CONTAINER_IS_RUNNING);
        }

        long updateResult = containerEntityRepository.updateContainerStatus(targetContainer.getPk(), ContainerStatusEnum.Running);
        return StartContainerResponseDto.builder()
                .startResult(0 != updateResult)
                .build();
    }

    /**
     * 예상 명령어 : docker stop ${컨테이너 이름}
     * 예상 인자 : 없음
     * 참고사항 :
     *          컨테이너의 isRm 옵션이 활성화 되었을 경우 stop 명령은 rm 명령으로 바뀐다.
     *
     * @param oauthServiceId 사용자 기본 정보
     * @param request 명령어의 추가 요구사항 List(인자 값)
     * @return 컨테이너 시작 성공유무
     */
    public StopContainerResponseDto stopContainer(String oauthServiceId, DockerCommandRequestDto request) {
        String containerName = getContainerNameForOneArgCommand(request.argCommands());

        ContainerEntity targetContainer = containerEntityRepository.findContainerByOAuthServiceAndContainerName(oauthServiceId, containerName);
        if (false == ContainerStatusEnum.Running.equals(targetContainer.getStatus())) {
            throw new CustomBadRequestException(ErrorCode.NOT_EXIST_RUNNING_CONTAINER);
        }

        boolean updateResult = false;
        if (targetContainer.isStopRm()) {
            DockerCommandRequestDto argCommands = DockerCommandRequestDto.builder()
                    .argCommands(List.of(targetContainer.getName()))
                    .build();

            RmContainerResponseDto rmContainerResponse = this.rmContainer(oauthServiceId, argCommands);

            updateResult = rmContainerResponse.rmResult();
        } else {
            long updateStatusResult = containerEntityRepository.updateContainerStatus(targetContainer.getPk(), ContainerStatusEnum.Paused);
            updateResult = (0 != updateStatusResult);
        }

        return StopContainerResponseDto.builder()
                .stopResult(updateResult)
                .build();
    }

    private String getContainerNameForOneArgCommand(List<String> argCommands) {
        if (1 != argCommands.size()) {
            throw new CustomBadRequestException(ErrorCode.ARGUMENT_COMMAND_NOT_VALID_EXCEPTION);
        }

        return argCommands.get(0);
    }
}
