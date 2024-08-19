package org.chs.restdockerapis.command.presentation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.chs.restdockerapis.command.application.CommandService;
import org.chs.restdockerapis.command.presentation.dto.CommandAnalysisRequestDto;
import org.chs.restdockerapis.command.presentation.dto.CommandAnalysisResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1/command")
public class CommandController {

    private final CommandService commandService;

    @PostMapping("/filter")
    public ResponseEntity<CommandAnalysisResponseDto> filteringCommand(@Valid @RequestBody CommandAnalysisRequestDto request) {
        return ResponseEntity.ok(commandService.filteringCommand(request));
    }
}
