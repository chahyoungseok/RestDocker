package org.chs.restdockerapis.common.exception.handler;

import org.chs.globalutils.dto.GlobalResponse;
import org.chs.restdockerapis.common.exception.HistoryException;
import org.chs.restdockerapis.common.exception.OpenApiException;
import org.chs.restdockerapis.common.exception.RestDockerException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @Order(value = 0)
    @ExceptionHandler(RestDockerException.class)
    public ResponseEntity<GlobalResponse> handlerCommonException(RestDockerException restDockerException) {
        log.error(restDockerException.getMessage(), restDockerException);

        return restDockerException.makeResponseEntity();
    }

    @Order(value = 1)
    @ExceptionHandler(HistoryException.class)
    public void handlerCommonException(HistoryException historyException) {
        log.error(historyException.getMessage(), historyException);
    }

    @Order(value = 2)
    @ExceptionHandler(OpenApiException.class)
    public ResponseEntity<GlobalResponse> handlerCommonException(OpenApiException openApiException) {
        log.error(openApiException.getMessage(), openApiException);

        return openApiException.makeResponseEntity();
    }

    @Order(value = 3)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handlerCommonException(MethodArgumentNotValidException exception) {
        log.error(exception.getMessage(), exception);

        return ResponseEntity.badRequest().body(exception.getMessage());
    }

    @Order(value = 4)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handlerCommonException(Exception exception) {
        log.error(exception.getMessage(), exception);

        return ResponseEntity.internalServerError().body(exception.getMessage());
    }


}
