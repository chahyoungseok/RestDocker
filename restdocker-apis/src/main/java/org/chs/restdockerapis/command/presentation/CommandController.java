package org.chs.restdockerapis.command.presentation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.chs.restdockerapis.command.application.CommandService;
import org.chs.restdockerapis.command.presentation.dto.CommandRequestDto;
import org.chs.restdockerapis.common.argument_resolver.annotation.GetRequester;
import org.chs.restdockerapis.common.argument_resolver.dto.GetRequesterDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.concurrent.ExecutionException;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1/command")
public class CommandController {

    private final CommandService commandService;

//    @Auth
    @PostMapping("/analysis")
    public ResponseEntity<Object> analysisCommand(@GetRequester GetRequesterDto requesterInfo, @Valid @RequestBody CommandRequestDto request) throws InterruptedException, ExecutionException {

        return ResponseEntity.ok(commandService.analysisCommand(requesterInfo, request));
    }
}
