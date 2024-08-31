package org.chs.domain.network.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.chs.domain.container.dto.ContainerElements;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NetworkDetailElements {
    private LocalDateTime createDate;
    private LocalDateTime updateDate;
    private String name;
    private String subnet;
    private String ipRange;
    private String gateway;
    private Boolean enableIcc;
    private Integer mtu;
    private List<ContainerElements> containerInfo;

    public void setContainerInfo(List<ContainerElements> containerElementsList){
        this.containerInfo = containerElementsList;
    }
}
