package org.chs.restdockerapis.common.jwt.principal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import org.chs.domain.common.enumerate.ThirdPartyEnum;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Builder
@AllArgsConstructor
public class AccountPrincipalDetails implements UserDetails {

    private ThirdPartyEnum thirdPartyType;
    private String oAuthServiceId;
    private String oAuthAccessToken;
    private String oAuthRefreshToken;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return null;
    }

    public String getAccountId() {
        return this.oAuthServiceId;
    }

    public String getOAuthAccessToken() {
        return this.oAuthAccessToken;
    }

    public ThirdPartyEnum getThirdPartyType() {
        return this.thirdPartyType;
    }

    public String getOAuthRefreshToken() {
        return this.oAuthRefreshToken;
    }
}
