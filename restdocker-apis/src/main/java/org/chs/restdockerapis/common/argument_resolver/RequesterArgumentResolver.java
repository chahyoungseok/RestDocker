package org.chs.restdockerapis.common.argument_resolver;

import org.chs.restdockerapis.common.argument_resolver.annotation.GetRequester;
import org.chs.restdockerapis.common.argument_resolver.dto.GetRequesterDto;
import jakarta.servlet.http.HttpServletRequest;
import org.chs.restdockerapis.common.jwt.dto.AccountPrincipalDetails;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class RequesterArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(GetRequesterDto.class) &&
                parameter.hasParameterAnnotation(GetRequester.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest httpServletRequest = (HttpServletRequest) webRequest.getNativeRequest();
        String ipAddress = httpServletRequest.getRemoteAddr();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (null == authentication || null == authentication.getAuthorities()
                || (null != authentication.getPrincipal() && authentication.getPrincipal().equals("anonymousUser"))) { // JWT 가 없는 API 인 경우

            return GetRequesterDto.builder()
                    .ipAddress(ipAddress)
                    .build();
        }

        AccountPrincipalDetails accountPrincipalDetails = (AccountPrincipalDetails) authentication.getPrincipal();

        return GetRequesterDto.builder()
                .ipAddress(ipAddress)
                .id(accountPrincipalDetails.getAccountId())
                .oauthAccessToken(accountPrincipalDetails.getOAuthAccessToken())
                .oauthRefreshToken(accountPrincipalDetails.getOAuthRefreshToken())
                .build();
    }
}
