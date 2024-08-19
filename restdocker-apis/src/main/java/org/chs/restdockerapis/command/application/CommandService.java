package org.chs.restdockerapis.command.application;

import org.chs.restdockerapis.command.enumerate.MainCommandEnum;
import org.chs.restdockerapis.command.enumerate.SubCommandEnum;
import org.chs.restdockerapis.command.presentation.dto.CommandRequestDto;
import org.chs.restdockerapis.command.presentation.dto.SeparateRequestDto;
import org.chs.restdockerapis.common.argument_resolver.dto.GetRequesterDto;
import org.chs.restdockerapis.common.exception.CustomBadRequestException;
import org.chs.restdockerapis.common.exception.ErrorCode;
import org.chs.restdockerapis.image.application.ImageService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CommandService {

    private final ImageService dockerImageService;
    private List<String> imageSubCommands;
    private List<String> networkSubCommands;
    private List<String> containerSubCommands;

    public CommandService(ImageService dockerImageService) {
        this.dockerImageService = dockerImageService;
        this.imageSubCommands = List.of("ls", "inspect", "rm", "pull");
        this.networkSubCommands = List.of("ls", "inspect", "create", "rm");
        this.containerSubCommands = List.of("ls", "ps", "rename", "create", "run", "start", "stop", "exec", "rm", "inspect");
    }

    public Object analysisCommand(GetRequesterDto requesterInfo, CommandRequestDto request) {
        SeparateRequestDto analysisResult = analysisCommand(request.getCommand());
        if (null == analysisResult) {
            throw new CustomBadRequestException(ErrorCode.COMMON_BAD_REQUEST_ERROR_EXCEPTION);
        }

        return requestSeparateApi(
                requesterInfo,
                analysisResult.mainCommand(),
                analysisResult.subCommand(),
                analysisResult.argCommand()
        );
    }

    /**
     * 분석 규칙
     * 1. 명령어를 공백 기준으로 나누어 배열을 만든다.
     * 2. 첫번째 인자가 docker 가 아닌 경우를 걸러낸다.
     * 3. 명령어의 생략어들을 정규화 시킨다.
     * 4. docker Help 의 경우를 고려한다.
     * 5. argCommand 를 추출한다.
     * 6. SeparateRequestDto 를 완성한다.
     */
    private SeparateRequestDto analysisCommand(String requestCommand) {
        SeparateRequestDto separateRequestDto = null;

        String[] commands = requestCommand.split("\\s+");

        firstCommandCheck(commands[0]);
        commands = dockerNormalization(commands);

        if (null != (separateRequestDto = dockerHelp(commands[1]))) {
            return separateRequestDto;
        }

        // 인덱스 3부터 검사해서 인자 빼오기
        Map<String, List<String>> argCommand = extractArgCommand(commands);

        if (null != (separateRequestDto = dockerImages(commands, argCommand))) {
            return separateRequestDto;
        }

        if (null != (separateRequestDto = dockerSeparate(commands, argCommand, imageSubCommands, MainCommandEnum.IMAGE.name().toLowerCase()))) {
            return separateRequestDto;
        }

        if (null != (separateRequestDto = dockerSeparate(commands, argCommand, networkSubCommands, MainCommandEnum.NETWORK.name().toLowerCase()))) {
            return separateRequestDto;
        }

        if (null != (separateRequestDto = dockerSeparate(commands, argCommand, containerSubCommands, MainCommandEnum.CONTAINER.name().toLowerCase()))) {
            return separateRequestDto;
        }

        return null;
    }

    private Map<String, List<String>> extractArgCommand(String[] commands) {
        Map<String, List<String>> argCommands = new HashMap<>();

        int argCommandStartNumber = 3;
        if (commands[1].equals("images")) {
            argCommandStartNumber = 2;
        }

        for (int commandIndex=argCommandStartNumber; commandIndex<commands.length;commandIndex++) {

            if (commands[commandIndex].startsWith("-")) {

                // - 옵션의 추가 인자가 있는지 확인
                if (commands.length > commandIndex + 1 && false == commands[commandIndex + 1].startsWith("-")) {

                    // Key 값이 이미 있는지 확인 후 저장
                    List<String> existKey = argCommands.get(commands[commandIndex]);
                    if (null == existKey) {
                        argCommands.put(commands[commandIndex], makeArrayList(commands[commandIndex + 1]));
                    }
                    else {
                        existKey.add(commands[commandIndex + 1]);
                    }

                    // 추가 인자가 저장되어 그 다음 요소는 건너 뜀
                    commandIndex++;
                }
                else {
                    // - 옵션이 있지만 추가 인자가 없는 조건
                    argCommands.put(commands[commandIndex], null);
                }
            }
            else {
                // - 옵션이 없는 일반 인자값
                argCommands.put(commands[commandIndex], null);
            }
        }

        return argCommands;
    }

    public Object requestSeparateApi(GetRequesterDto requesterInfo, MainCommandEnum mainCommand, SubCommandEnum subCommand, Map<String, List<String>> argCommand) {
        switch (mainCommand) {
            case IMAGE -> {

                switch (subCommand) {
                    case READ_ALL -> {
                        return dockerImageService.readImage(requesterInfo, argCommand);
                    }
                    case READ_SPECIFIC_NAME -> {
                        return null;
                    }
                    case LS -> {
                        return null;
                    }
                    case INSPECT -> {
                        return null;
                    }
                    case RM -> {
                        return null;
                    }
                    case PULL -> {
                        return null;
                    }
                    default -> throw new CustomBadRequestException(ErrorCode.NOT_CORRECT_SUBCOMMAND);
                }
            }
            case NETWORK -> {

                switch (subCommand) {
                    case LS -> {
                        return null;
                    }
                    case INSPECT -> {
                        return null;
                    }
                    case CREATE -> {
                        return null;
                    }
                    case RM -> {
                        return null;
                    }
                    default -> throw new CustomBadRequestException(ErrorCode.NOT_CORRECT_SUBCOMMAND);
                }
            }
            case CONTAINER -> {

                switch (subCommand) {
                    case LS -> {
                        return null;
                    }
                    case PS -> {
                        return null;
                    }
                    case RENAME -> {
                        return null;
                    }
                    case CREATE -> {
                        return null;
                    }
                    case RUN -> {
                        return null;
                    }
                    case START -> {
                        return null;
                    }
                    case STOP -> {
                        return null;
                    }
                    case EXEC -> {
                        return null;
                    }
                    case RM -> {
                        return null;
                    }
                    case INSPECT -> {
                        return null;
                    }
                    default -> throw new CustomBadRequestException(ErrorCode.NOT_CORRECT_SUBCOMMAND);
                }
            }
            case HELP -> {
                break;
            }
            default -> throw new CustomBadRequestException(ErrorCode.NOT_CORRECT_MAINCOMMAND);
        }

        throw new CustomBadRequestException(ErrorCode.NOT_CORRECT_MAINCOMMAND);
    }

    private void firstCommandCheck(String firstCommand) {
        if (null == firstCommand) {
            throw new CustomBadRequestException(ErrorCode.BLANK_COMMAND);
        }

        if (false == firstCommand.equals("docker")) {
            throw new CustomBadRequestException(ErrorCode.COMMAND_NEED_DOCKER);
        }
    }

    private SeparateRequestDto dockerHelp(String secondCommand) {
        if (null == secondCommand) {
            return makeSeparateRequestDto(MainCommandEnum.HELP, null, null);
        }
        return null;
    }

    private SeparateRequestDto dockerImages(String[] commands, Map<String, List<String>> argCommand) {
        if (commands[1].equals("images")) {
            if (null == commands[2]) {
                return makeSeparateRequestDto(MainCommandEnum.IMAGE, SubCommandEnum.READ_ALL, null);
            } else {
                return makeSeparateRequestDto(MainCommandEnum.IMAGE, SubCommandEnum.READ_SPECIFIC_NAME, argCommand);
            }
        }
        return null;
    }

    private SeparateRequestDto dockerSeparate(String[] commands, Map<String, List<String>> argCommand, List<String> candidateSubCommands, String keyword) {
        if (false == commands[1].equals(keyword)) {
            return null;
        }

        if (null == commands[2] || false == candidateSubCommands.contains(commands[2])) {
            throw new CustomBadRequestException(ErrorCode.NOT_CORRECT_SUBCOMMAND);
        }

        for (String subCommand : candidateSubCommands) {
            if (commands[2].equals(subCommand)) {
                return makeSeparateRequestDto(MainCommandEnum.valueOf(keyword.toUpperCase()), SubCommandEnum.valueOf(subCommand.toUpperCase()), argCommand);
            }
        }

        return null;
    }

    private String[] dockerNormalization(String[] commands) {

        String secondToken = commands[1];

        if (containerSubCommands.contains(secondToken)) {
            return insertOmitContainer(commands);
        }

        if (secondToken.equals("rmi")) {
            return changeRmi(commands);
        }

        return commands;
    }

    private String[] insertOmitContainer(String[] oldCommands) {
        String[] newCommands = new String[oldCommands.length + 1];

        for (int commandIndex = 2; commandIndex < oldCommands.length + 1; commandIndex++) {
            newCommands[commandIndex] = oldCommands[commandIndex - 1];
        }

        newCommands[0] = oldCommands[0];
        newCommands[1] = "container";
        return newCommands;
    }

    private String[] changeRmi(String[] oldCommands) {
        String[] newCommands = new String[oldCommands.length + 1];

        for (int commandIndex = 2; commandIndex < oldCommands.length + 1; commandIndex++) {
            newCommands[commandIndex] = oldCommands[commandIndex - 1];
        }

        newCommands[0] = oldCommands[0];
        newCommands[1] = "image";
        newCommands[2] = "rm";

        return newCommands;
    }

    private ArrayList<String> makeArrayList(String element) {
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add(element);

        return arrayList;
    }

    private SeparateRequestDto makeSeparateRequestDto(MainCommandEnum mainCommand, SubCommandEnum subCommand, Map<String, List<String>> argCommand) {
        return SeparateRequestDto.builder()
                .mainCommand(mainCommand)
                .subCommand(subCommand)
                .argCommand(argCommand)
                .build();
    }
}
