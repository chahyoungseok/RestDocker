package org.chs.tokenissuer.common.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.chs.globalutils.dto.GlobalResponse;
import org.chs.tokenissuer.common.exception.CustomTokenException;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class TokenIssuerGlobalExceptionHandler {

    @Order(value = 0)
    @ExceptionHandler(CustomTokenException.class)
    public ResponseEntity<GlobalResponse> handlerCommonException(CustomTokenException restDockerException) {
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
