package org.chs.domain.container.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.chs.domain.container.enumerate.ContainerStatusEnum;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ContainerElements {
    private LocalDateTime createDate;

    private LocalDateTime updateDate;

    private String name;

    private String imageName;

    private String imageTag;

    private String privateIp;

    private String outerPort;

    private String innerPort;

    private ContainerStatusEnum status;
}
