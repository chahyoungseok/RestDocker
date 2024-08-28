package org.chs.domain.network.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NetworkElements {
    private LocalDateTime createDate;
    private LocalDateTime updateDate;
    private String name;
    private String subnet;
    private String ipRange;
    private String gateway;
}
