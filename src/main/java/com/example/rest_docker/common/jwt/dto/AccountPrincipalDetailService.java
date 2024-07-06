package com.example.rest_docker.common.jwt.dto;

import com.example.rest_docker.account.domain.AccountRepository;
import com.example.rest_docker.account.domain.entity.AccountEntity;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class AccountPrincipalDetailService implements UserDetailsService {

    private final AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String accountId) throws UsernameNotFoundException {
        Optional<AccountEntity> optionalAccount = accountRepository.findByOauthServiceIdEquals(accountId);

        if (optionalAccount.isPresent()) {
            return new AccountPrincipalDetails(optionalAccount.get());
        }

        return null;
    }
}
