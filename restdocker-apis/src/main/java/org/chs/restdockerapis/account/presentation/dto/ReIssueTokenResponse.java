package org.chs.restdockerapis.account.presentation.dto;

import lombok.Builder;

@Builder
public record ReIssueTokenResponse (
        String accessToken
){}
