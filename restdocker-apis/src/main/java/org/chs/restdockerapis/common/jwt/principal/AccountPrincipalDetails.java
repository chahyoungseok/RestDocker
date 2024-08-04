package org.chs.restdockerapis.common.jwt.principal;

import lombok.AllArgsConstructor;
import org.chs.domain.account.entity.AccountEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@AllArgsConstructor
public class AccountPrincipalDetails implements UserDetails {

    private AccountEntity account;

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
        return this.account.getOauthServiceId();
    }

    public String getAccessToken() {
        return this.account.getAccessToken();
    }

    public String getRefreshToken() {
        return this.account.getRefreshToken();
    }

    public String getNickname() {
        return this.account.getNickname();
    }

    public String getOAuthAccessToken() {
        return this.account.getThirdPartyAccessToken();
    }

    public String getOAuthRefreshToken() {
        return this.account.getThirdPartyRefreshToken();
    }
}
