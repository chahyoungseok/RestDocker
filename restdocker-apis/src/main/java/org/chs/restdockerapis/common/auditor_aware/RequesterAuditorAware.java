package org.chs.restdockerapis.common.auditor_aware;

import org.chs.restdockerapis.common.jwt.principal.AccountPrincipalDetails;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class RequesterAuditorAware implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (null == authentication || !authentication.isAuthenticated()
                || (null != authentication.getPrincipal() && authentication.getPrincipal().equals("anonymousUser"))) {
            return Optional.empty();
        }

        AccountPrincipalDetails accountPrincipalDetails = (AccountPrincipalDetails) authentication.getPrincipal();
        return Optional.of(accountPrincipalDetails.getAccountId());
    }
}
