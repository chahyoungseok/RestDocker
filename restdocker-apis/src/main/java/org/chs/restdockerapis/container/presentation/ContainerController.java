package org.chs.restdockerapis.container.presentation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.chs.restdockerapis.common.aop.annotation.Auth;
import org.chs.restdockerapis.common.argument_resolver.annotation.GetRequester;
import org.chs.restdockerapis.common.argument_resolver.dto.GetRequesterDto;
import org.chs.restdockerapis.container.application.ContainerService;
import org.chs.restdockerapis.container.presentation.dto.*;
import org.chs.restdockerapis.image.presentation.dto.DockerCommandRequestDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1/container")
public class ContainerController {

    private final ContainerService containerService;

    @Auth
    @PostMapping("/ls")
    public ResponseEntity<LsContainerResponseDto> lsContainer(@GetRequester GetRequesterDto requesterInfo, @Valid @RequestBody DockerCommandRequestDto request) {
        return ResponseEntity.ok(containerService.lsContainer(requesterInfo, request));
    }

    @Auth
    @PostMapping("/inspect")
    public ResponseEntity<InspectContainerResponseDto> inspectContainer(@GetRequester GetRequesterDto requesterInfo, @Valid @RequestBody DockerCommandRequestDto request) {
        return ResponseEntity.ok(containerService.inspectContainer(requesterInfo, request));
    }

    @Auth
    @PostMapping("/rename")
    public ResponseEntity<RenameContainerResponseDto> renameContainer(@GetRequester GetRequesterDto requesterInfo, @Valid @RequestBody DockerCommandRequestDto request) {
        return ResponseEntity.ok(containerService.renameContainer(requesterInfo, request));
    }

    @Auth
    @PostMapping("/create")
    public ResponseEntity<CreateContainerResponseDto> createContainer(@GetRequester GetRequesterDto requesterInfo, @Valid @RequestBody DockerCommandRequestDto request) {
        return ResponseEntity.ok(containerService.createContainer(requesterInfo, request));
    }

    @Auth
    @PostMapping("/rm")
    public ResponseEntity<RmContainerResponseDto> rmContainer(@GetRequester GetRequesterDto requesterInfo, @Valid @RequestBody DockerCommandRequestDto request) {
        return ResponseEntity.ok(containerService.rmContainer(requesterInfo.id(), request));
    }

    @Auth
    @PostMapping("/run")
    public ResponseEntity<RunContainerResponseDto> runContainer(@GetRequester GetRequesterDto requesterInfo, @Valid @RequestBody DockerCommandRequestDto request) {
        return ResponseEntity.ok(containerService.runContainer(requesterInfo, request));
    }

    @Auth
    @PostMapping("/start")
    public ResponseEntity<StartContainerResponseDto> startContainer(@GetRequester GetRequesterDto requesterInfo, @Valid @RequestBody DockerCommandRequestDto request) {
        return ResponseEntity.ok(containerService.startContainer(requesterInfo, request));
    }

    @Auth
    @PostMapping("/stop")
    public ResponseEntity<StopContainerResponseDto> stopContainer(@GetRequester GetRequesterDto requesterInfo, @Valid @RequestBody DockerCommandRequestDto request) {
        return ResponseEntity.ok(containerService.stopContainer(requesterInfo, request));
    }
}
