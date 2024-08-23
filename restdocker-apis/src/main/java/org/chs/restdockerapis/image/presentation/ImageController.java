package org.chs.restdockerapis.image.presentation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.chs.restdockerapis.common.aop.annotation.Auth;
import org.chs.restdockerapis.common.argument_resolver.annotation.GetRequester;
import org.chs.restdockerapis.common.argument_resolver.dto.GetRequesterDto;
import org.chs.restdockerapis.image.application.ImageService;
import org.chs.restdockerapis.image.presentation.dto.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1/image")
public class ImageController {

    private final ImageService imageService;

    @Auth
    @PostMapping("/ls")
    public ResponseEntity<LsImageResponseDto> lsImage(@GetRequester GetRequesterDto requesterInfo, @Valid @RequestBody DockerCommandRequestDto request) {
        return ResponseEntity.ok(imageService.lsImage(requesterInfo, request));
    }

    @Auth
    @PostMapping("/pull")
    public ResponseEntity<PullImageResponseDto> pullImage(@GetRequester GetRequesterDto requesterInfo, @Valid @RequestBody DockerCommandRequestDto request) {
        return ResponseEntity.ok(imageService.pullImage(requesterInfo, request));
    }

    @Auth
    @PostMapping("/inspect")
    public ResponseEntity<InspectImageResponseDto> inspectImage(@GetRequester GetRequesterDto requesterInfo, @Valid @RequestBody DockerCommandRequestDto request) {
        return ResponseEntity.ok(imageService.inspectImage(requesterInfo, request));
    }

    @Auth
    @PostMapping("/rm")
    public ResponseEntity<RmImageResponseDto> rmImage(@GetRequester GetRequesterDto requesterInfo, @Valid @RequestBody DockerCommandRequestDto request) {
        return ResponseEntity.ok(imageService.rmImage(requesterInfo, request));
    }

}
