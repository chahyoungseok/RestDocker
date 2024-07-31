package org.chs.restdockerapis.account.presentation.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReIssueTokenResponse {
    private String accessToken;
}
