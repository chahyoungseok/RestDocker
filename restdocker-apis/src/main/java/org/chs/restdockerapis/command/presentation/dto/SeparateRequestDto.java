package org.chs.restdockerapis.command.presentation.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.chs.restdockerapis.command.enumerate.MainCommandEnum;
import org.chs.restdockerapis.command.enumerate.SubCommandEnum;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SeparateRequestDto {
        @NotEmpty
        private MainCommandEnum mainCommand;

        private SubCommandEnum subCommand;

        private List<String> argCommand;

        public SeparateRequestDto psToLs(){
                if (MainCommandEnum.CONTAINER.equals(mainCommand)
                        && SubCommandEnum.PS.equals(subCommand)) {
                        subCommand = SubCommandEnum.LS;
                }

                return this;
        }
}
