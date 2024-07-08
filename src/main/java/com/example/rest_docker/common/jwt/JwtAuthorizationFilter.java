package com.example.rest_docker.common.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.example.rest_docker.account.domain.AccountRepository;
import com.example.rest_docker.account.domain.entity.AccountEntity;
import com.example.rest_docker.common.exception.RestDockerExceptionCode;
import com.example.rest_docker.common.jwt.dto.AccountPrincipalDetails;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

// OncePerRequestFilter : 어느 서블릿 컨테이너에서나 DispatcherServlet 앞단에서 요청 당 한 번의 실행을 보장하는것을 목표로 합니다.

@Slf4j
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtProperties jwtProperties;
    private final AccountRepository accountRepository;
    private final ObjectMapper objectMapper;
    private Map<String, Object> body;

    public JwtAuthorizationFilter(AccountRepository accountRepository, JwtProperties jwtProperties, ObjectMapper objectMapper) {
        this.accountRepository = accountRepository;
        this.jwtProperties = jwtProperties;
        this.objectMapper = objectMapper;
        body = new HashMap<>();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String jwtHeader = request.getHeader(jwtProperties.getHEADER_STRING());

        // header가 있는지 확인
        if(null == jwtHeader || false == jwtHeader.startsWith(jwtProperties.getTOKEN_PREFIX())) {
            filterChain.doFilter(request, response);
            return;
        }

        log.info("Authentication Filter - jwtHeader : {}", jwtHeader);

        String token = request.getHeader(jwtProperties.getHEADER_STRING()).replace(jwtProperties.getTOKEN_PREFIX(), "");
        String accountId = null;

        try {
            accountId = JWT.require(Algorithm.HMAC512(JwtProperties.SECRET_KEY)).build().verify(token).getClaim("id").asString();
        } catch (TokenExpiredException e) {
            logger.warn("the token is expired and not valid anymore", e);
            sendErrorResponse(request, response, RestDockerExceptionCode.JWT_TOKEN_EXPIRED_EXCEPTION);
        }

        if(null != accountId) {
            log.info("Authentication Filter - 서명이 정상적으로 됨");

            Optional<AccountEntity> optionalJwtTokenAccount = accountRepository.findByOauthServiceIdEquals(accountId);
            if (false == optionalJwtTokenAccount.isPresent()) {
                sendErrorResponse(request, response, RestDockerExceptionCode.JWT_TOKEN_VALID_EXCEPTION);
            }

            Authentication authentication = getAuthorities(optionalJwtTokenAccount.get());
            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request, response);
        }
    }

    private Authentication getAuthorities(AccountEntity account) {
        return new UsernamePasswordAuthenticationToken(new AccountPrincipalDetails(account), null, null);
    }

    private void sendErrorResponse(HttpServletRequest request, HttpServletResponse response, RestDockerExceptionCode exception) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        body.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        body.put("error", "Unauthorized");
        body.put("HttpStatus", exception.getHttpStatus());
        body.put("resultCode", exception.getResultCode());
        body.put("message", exception.getDescription());
        body.put("path", request.getServletPath());

        objectMapper.writeValue(response.getOutputStream(), body);
        response.setStatus(HttpServletResponse.SC_OK);

        body.clear();
    }
}
