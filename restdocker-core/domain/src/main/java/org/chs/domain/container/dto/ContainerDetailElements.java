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
public class ContainerDetailElements {
    private LocalDateTime createDate;

    private LocalDateTime updateDate;

    private String imageName;

    private String imageTag;

    private String name;

    private ContainerStatusEnum status;

    private String outerPort;

    private String innerPort;

    private String privateIp;

    private boolean stopRm;
}
