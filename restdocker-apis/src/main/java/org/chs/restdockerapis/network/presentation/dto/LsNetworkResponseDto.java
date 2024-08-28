package org.chs.restdockerapis.network.presentation.dto;

import lombok.Builder;
import org.chs.domain.network.dto.NetworkElements;

import java.util.List;

@Builder
public record LsNetworkResponseDto(
        List<NetworkElements> lsNetworkElements
) {}
