package org.chs.restdockerapis.network.presentation;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.chs.restdockerapis.common.aop.annotation.Auth;
import org.chs.restdockerapis.common.argument_resolver.annotation.GetRequester;
import org.chs.restdockerapis.common.argument_resolver.dto.GetRequesterDto;
import org.chs.restdockerapis.image.presentation.dto.DockerCommandRequestDto;
import org.chs.restdockerapis.network.application.NetworkService;
import org.chs.restdockerapis.network.presentation.dto.LsNetworkResponseDto;
import org.chs.restdockerapis.network.presentation.dto.CreateNetworkResponseDto;
import org.chs.restdockerapis.network.presentation.dto.InspectNetworkResponseDto;
import org.chs.restdockerapis.network.presentation.dto.RmNetworkResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1/network")
public class NetworkController {

    private final NetworkService networkService;

    @Auth
    @PostMapping("/ls")
    public ResponseEntity<LsNetworkResponseDto> lsNetwork(@GetRequester GetRequesterDto requesterInfo, @Valid @RequestBody DockerCommandRequestDto request) {
        return ResponseEntity.ok(networkService.lsNetwork(requesterInfo, request));
    }

    @Auth
    @PostMapping("/inspect")
    public ResponseEntity<InspectNetworkResponseDto> inspectNetwork(@GetRequester GetRequesterDto requesterInfo, @Valid @RequestBody DockerCommandRequestDto request) {
        return ResponseEntity.ok(networkService.inspectNetwork(requesterInfo, request));
    }

    @Auth
    @PostMapping("/create")
    public ResponseEntity<CreateNetworkResponseDto> createNetwork(@GetRequester GetRequesterDto requesterInfo, @Valid @RequestBody DockerCommandRequestDto request) {
        return ResponseEntity.ok(networkService.createNetwork(requesterInfo, request));
    }

    @Auth
    @PostMapping("/rm")
    public ResponseEntity<RmNetworkResponseDto> rmNetwork(@GetRequester GetRequesterDto requesterInfo, @Valid @RequestBody DockerCommandRequestDto request) {
        return ResponseEntity.ok(networkService.rmNetwork(requesterInfo, request));
    }
}
