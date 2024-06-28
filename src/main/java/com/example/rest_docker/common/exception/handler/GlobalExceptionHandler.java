package com.example.rest_docker.common.exception.handler;

import com.example.rest_docker.common.dto.GlobalResponse;
import com.example.rest_docker.common.exception.RestDockerException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Order(value = 0)
    @ExceptionHandler(RestDockerException.class)
    public ResponseEntity<GlobalResponse> handlerCommonException(RestDockerException restDockerException) {
        log.error(restDockerException.getMessage(), restDockerException);

        return restDockerException.makeResponseEntity();
    }

    @Order(value = 1)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handlerCommonException(Exception exception) {
        log.error(exception.getMessage(), exception);

        return ResponseEntity.internalServerError().body(exception.getMessage());
    }


}
