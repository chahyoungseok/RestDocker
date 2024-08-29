package org.chs.domain.container.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ContainerValidElementsDto {

    private String containerName;

    private String outerPort;
}
