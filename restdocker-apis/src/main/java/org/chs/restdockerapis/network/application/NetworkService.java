package org.chs.restdockerapis.network.application;

import lombok.RequiredArgsConstructor;
import org.chs.domain.account.AccountRepository;
import org.chs.domain.account.entity.AccountEntity;
import org.chs.domain.common.enumerate.ThirdPartyEnum;
import org.chs.domain.network.NetworkEntityRepository;
import org.chs.domain.network.dto.NetworkDetailElements;
import org.chs.domain.network.dto.NetworkElements;
import org.chs.domain.network.entity.NetworkEntity;
import org.chs.restdockerapis.common.argument_resolver.dto.GetRequesterDto;
import org.chs.restdockerapis.common.exception.CustomBadRequestException;
import org.chs.restdockerapis.common.exception.ErrorCode;
import org.chs.restdockerapis.common.util.ListUtils;
import org.chs.restdockerapis.image.presentation.dto.DockerCommandRequestDto;
import org.chs.restdockerapis.network.presentation.dto.*;
import org.chs.restdockerapis.network.util.AddressUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class NetworkService {

    private final AccountRepository accountRepository;
    private final NetworkEntityRepository dockerNetworkRepository;

    private final ListUtils listUtils;
    private final AddressUtils addressUtils;

    /**
     * 예상 명령어 : docker network ls
     * 예상 인자값 : 없음
     *
     * @param requesterInfo 사용자 기본 정보 (IP, OAuthServiceId, AccessToken, RefreshToken)
     * @param request ArgCommands : 명령어의 추가 요구사항 List(인자 값)
     * @return DockerNetwork 조회의 결과 List
     */
    @Transactional(readOnly = true)
    public LsNetworkResponseDto lsNetwork(GetRequesterDto requesterInfo, DockerCommandRequestDto request) {
        if (false == listUtils.isBlank(request.getArgCommands())) {
            throw new CustomBadRequestException(ErrorCode.ARGUMENT_COMMAND_NOT_VALID_EXCEPTION);
        }

        List<NetworkElements> networkElementList = dockerNetworkRepository.findByOAuthServiceId(requesterInfo.id());

        return LsNetworkResponseDto.builder()
                .lsNetworkElements(networkElementList)
                .build();
    }

    /**
     * 예상 명령어 : docker network inspect ${도커 네트워크 이름}
     * 예상 인자값 : 없음
     *
     * @param requesterInfo 사용자 기본 정보 (IP, OAuthServiceId, AccessToken, RefreshToken)
     * @param request ArgCommands : 명령어의 추가 요구사항 List(인자 값)
     * @return 특정 DockerNetwork 조회의 자세한 결과
     */
    @Transactional(readOnly = true)
    public InspectNetworkResponseDto inspectNetwork(GetRequesterDto requesterInfo, DockerCommandRequestDto request) {
        String networkName = getNetworkNameForOneArgCommand(request.getArgCommands());

        NetworkDetailElements inspectedNetworkDetailElements = dockerNetworkRepository.inspectNetwork(requesterInfo.id(), networkName);

        return InspectNetworkResponseDto.builder()
                .inspectNetworkDetailElements(inspectedNetworkDetailElements)
                .build();
    }

    /**
     * 예상 명령어 : docker network create ${도커 네트워크 이름}
     * 예상 인자값 :
     *              --gateway ${IPv4}
     *              --subnet 172.123.0.0/16
     *              --ip-range 172.123.1.0/24
     *              --opt com.docker.network.bridge.enable_icc={}
     *              --opt com.docker.network.driver.mtu={}
     *              ${도커 네트워크 이름}
     *
     * 참고사항 :
     *              도커 네트워크 이름이 마지막에 있는게 아니어도 상관없음
     *              Gateway나 IpRange를 설정하려면 Subnet 설정을 해야한다
     *              아무 설정없이 Create하게되면 Subnet과 Gateway만 생성된다
     *              IPRange 와 Gateway는 Subnet 안의 대역이어야한다
     *              Subnet 만 설정하면 Gateway와 IPRange가 설정되지않는다
     *              172.123.0.1/16 와 같이 알맞지 않은 표현은 172.123.0.0/16 로 정정하여 에러를 보낸다
     *
     * @param requesterInfo 사용자 기본 정보 (IP, OAuthServiceId, AccessToken, RefreshToken)
     * @param request ArgCommands : 명령어의 추가 요구사항 List(인자 값)
     * @return 생성된 DockerNetwork 정보
     */
    public CreateNetworkResponseDto createNetwork(GetRequesterDto requesterInfo, DockerCommandRequestDto request) {
        if (listUtils.isBlank(request.getArgCommands())) {
            throw new CustomBadRequestException(ErrorCode.ARGUMENT_COMMAND_NOT_VALID_EXCEPTION);
        }

        NetworkOptionDto castednetworkOptionDto = castCommandToMap(request.getArgCommands());
        NetworkOptionDto validedOptionDto = validNetworkOption(castednetworkOptionDto, requesterInfo.id());

        NetworkEntity savedNetwork = saveNetworkForOptionDto(validedOptionDto, requesterInfo.id(), requesterInfo.thirdPartyType());

        return CreateNetworkResponseDto.builder()
                .networkName(savedNetwork.getName())
                .build();
    }

    private NetworkOptionDto castCommandToMap(List<String> argCommands) {
        NetworkOptionDto networkOption = new NetworkOptionDto();

        for (String argCommand : argCommands) {

            if (argCommand.startsWith("-")) {
                String[] args = argCommand.split(" ");
                if (2 != args.length) {
                    throw new CustomBadRequestException(ErrorCode.ARGUMENT_COMMAND_NOT_VALID_EXCEPTION);
                }
                networkOption.putArgCommand(args);
            }
            else {
                if (argCommand.contains(" ") || null != networkOption.getName()) {
                    throw new CustomBadRequestException(ErrorCode.ARGUMENT_COMMAND_NOT_VALID_EXCEPTION);
                }
                networkOption.setName(argCommand);
            }
        }

        return networkOption;
    }

    private NetworkOptionDto validNetworkOption(NetworkOptionDto networkOption, String oauthServiceId) {
        if (false == networkOption.validNotExistSubnet()) {
            throw new CustomBadRequestException(ErrorCode.ARGS_NEED_SUBNET);
        }

        return validIpRange(
                validGateway(
                        validSubnet(networkOption, oauthServiceId)
                )
        );
    }

    private NetworkOptionDto validSubnet(NetworkOptionDto networkOption, String oauthServiceId) {
        if (null == networkOption.getSubnet()) {
            String subnet = automaticAllocationSubnet(oauthServiceId);
            String gateway = addressUtils.automaticAllocationGateway(subnet);

            networkOption.setAutomaticAddress(subnet, gateway);
        }
        if (false == addressUtils.validAddressRangeFormat(networkOption.getSubnet())) {
            throw new CustomBadRequestException(ErrorCode.NOT_VALID_ADDRESS_FORMAT);
        }
        return networkOption;
    }

    private NetworkOptionDto validGateway(NetworkOptionDto networkOption) {
        if (false == addressUtils.validAddressFormat(networkOption.getGateway())) {
            throw new CustomBadRequestException(ErrorCode.NOT_VALID_ADDRESS_FORMAT);
        }
        if (false == addressUtils.validGatewayIntoSubnet(networkOption.getSubnet(), networkOption.getGateway())) {
            throw new CustomBadRequestException(ErrorCode.MUST_GATEWAY_INTO_SUBNET);
        }
        return networkOption;
    }

    private NetworkOptionDto validIpRange(NetworkOptionDto networkOption) {
        if (null != networkOption.getIpRange()) {
            if (false == addressUtils.validAddressRangeFormat(networkOption.getIpRange())) {
                throw new CustomBadRequestException(ErrorCode.NOT_VALID_ADDRESS_FORMAT);
            }
            if (false == addressUtils.validIPRangeIntoSubnet(networkOption.getSubnet(), networkOption.getIpRange())) {
                throw new CustomBadRequestException(ErrorCode.MUST_IPRANGE_INTO_SUBNET);
            }
        }
        return networkOption;
    }


    private String automaticAllocationSubnet(String oauthServiceId) {
        List<NetworkElements> networkList = dockerNetworkRepository.findByOAuthServiceId(oauthServiceId);

        return addressUtils.automaticAllocationSubnet(
                networkList.stream()
                        .map(NetworkElements::getSubnet)
                        .toList()
        );
    }


    private NetworkEntity saveNetworkForOptionDto(NetworkOptionDto networkOptionDto, String oauthServiceId, ThirdPartyEnum thirdPartyType) {
        AccountEntity account = accountRepository.findByOauthServiceIdEqualsAndThirdPartyTypeEquals(oauthServiceId, thirdPartyType)
                .orElseThrow(() -> new CustomBadRequestException(ErrorCode.ACCOUNT_NOT_EXIST_OAUTH_ID_EXCEPTION));

        return dockerNetworkRepository.save(
                NetworkEntity.builder()
                        .account(account)
                        .name(networkOptionDto.getName())
                        .subnet(networkOptionDto.getSubnet())
                        .ipRange(networkOptionDto.getIpRange())
                        .gateway(networkOptionDto.getGateway())
                        .mtu(null == networkOptionDto.getMtu() ? 1000 : networkOptionDto.getMtu())
                        .enableIcc(null == networkOptionDto.getIcc() ? true : networkOptionDto.getIcc())
                        .build()
        );
    }

    /**
     * 여러 이미지도 한번에 삭제?
     * 예상 명령어 : docker network rm ${도커 네트워크 이름}
     * 예상 인자값 : 없음
     *
     * @param requesterInfo 사용자 기본 정보 (IP, OAuthServiceId, AccessToken, RefreshToken)
     * @param request ArgCommands : 명령어의 추가 요구사항 List(인자 값)
     * @return 특정 DockerNetwork 삭제의 성공유무
     */
    public RmNetworkResponseDto rmNetwork(GetRequesterDto requesterInfo, DockerCommandRequestDto request) {
        String networkName = getNetworkNameForOneArgCommand(request.getArgCommands());

        boolean deleteNetworkResult = dockerNetworkRepository.rmNetwork(requesterInfo.id(), networkName);

        return RmNetworkResponseDto.builder()
                .networkDeleteResult(deleteNetworkResult)
                .build();
    }

    private String getNetworkNameForOneArgCommand(List<String> argCommands) {
        if (1 != argCommands.size()) {
            throw new CustomBadRequestException(ErrorCode.ARGUMENT_COMMAND_NOT_VALID_EXCEPTION);
        }

        return argCommands.get(0);
    }
}
