package org.chs.restdockerapis.account.presentation.dto.common;


import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;

@Builder
public record OAuthLoginRequestDto(
        @NotEmpty
        String code
) {}
