package org.chs.restdockerapis.network.presentation.dto;

import lombok.Builder;
import org.chs.domain.network.dto.NetworkDetailElements;

@Builder
public record InspectNetworkResponseDto(
        NetworkDetailElements inspectNetworkDetailElements
) {}
