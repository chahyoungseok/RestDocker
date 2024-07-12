package com.example.rest_docker.common.aop.aspect;

import com.example.rest_docker.common.argument_resolver.dto.GetRequesterDto;
import com.example.rest_docker.common.exception.RestDockerException;
import com.example.rest_docker.common.exception.RestDockerExceptionCode;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Aspect
@Component
public class AuthAspectConfig {

    @Pointcut("@annotation(com.example.rest_docker.common.aop.annotation.Auth)")
    private void enableAuth() {};

    @Pointcut("execution(* *..*Controller.*(..))")
    private void allController(){}

    @Around("allController() && enableAuth()")
    public Object validToken(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("[Valid JWT] - Location : {}", joinPoint.getSignature());

        GetRequesterDto requesterDto = (GetRequesterDto) Arrays.stream(joinPoint.getArgs()).toList().get(0);
        if (null == requesterDto.oauthAccessToken()) { // JWT 가 없는 API 인 경우
            throw new RestDockerException(RestDockerExceptionCode.API_NEED_TOKEN_EXCEPTION);
        }
        return joinPoint.proceed();
    }
}
